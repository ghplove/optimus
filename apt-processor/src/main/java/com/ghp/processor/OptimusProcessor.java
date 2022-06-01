package com.ghp.processor;

import com.ghp.annotation.OptimusService;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.ghp.annotation.OptimusService"})
public class OptimusProcessor extends AbstractProcessor {

    public static final String META_INF_OPTIMUS = "META-INF/optimus/";
    static final String MISSING_SERVICES_ERROR = "No service interface provided for element!";
    /**
     * 接口类和其实现类的map
     */
    private HashMap<String, String> providers = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(OptimusService.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * <ol>
     *  <li> For each class annotated with {@link OptimusService}<ul>
     *      <li> Verify the {@link OptimusService} interface value is correct
     *      <li> Categorize the class by its service interface
     *      </ul>
     *
     *  <li> For each {@link OptimusService} interface <ul>
     *       <li> Create a file named {@code META-INF/services/<interface>}
     *       <li> For each {@link OptimusService} annotated class for this interface <ul>
     *           <li> Create an entry in the file
     *           </ul>
     *       </ul>
     * </ol>
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            return processImpl(annotations, roundEnv);
        } catch (Exception e) {
            // We don't allow exceptions of any kind to propagate to the compiler
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            fatalError(writer.toString());
            return true;
        }
    }

    private boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            // 语法树解析完成后，生成META-INF文件
            generateConfigFiles();
        } else {
            // 语法树解析阶段，扫描代码中的注解
            processAnnotations(annotations, roundEnv);
        }
        return true;
    }

    private void processAnnotations(Set<? extends TypeElement> annotations,
                                    RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(OptimusService.class);
        log(annotations.toString());
        log(elements.toString());

        for (Element element : elements) {
            TypeElement providerImplementer = (TypeElement) element;
            // 获取注解
            AnnotationMirror annotationMirror = Utils.getAnnotationMirror(element, OptimusService.class);
            // 获取注解中的value字段
            DeclaredType providerInterface = Utils.getValueField(annotationMirror);
            if (providerInterface == null) {
                error(MISSING_SERVICES_ERROR, element, annotationMirror);
                continue;
            }
            TypeElement providerType = (TypeElement) providerInterface.asElement();
            log("provider interface: " + providerType.getQualifiedName());
            log("provider implementer: " + providerImplementer.getQualifiedName());
            // 校验实现类是否实现了IOptimusManager
            if (!checkExtendOptimusManager(providerImplementer)) {
                String message = providerImplementer.getQualifiedName() + " does not implement IOptimusManager";
                error(message, element, annotationMirror);
                return;
            }
            // 校验实现类是否实现类声明的接口
            if (checkImplementer(providerImplementer, providerType)) {
                providers.put(Utils.getBinaryName(providerType), Utils.getBinaryName(providerImplementer));
            } else {
                String message = "ServiceProviders must implement their service provider interface. "
                        + providerImplementer.getQualifiedName() + " does not implement "
                        + providerType.getQualifiedName();
                error(message, element, annotationMirror);
            }
        }
    }

    private void generateConfigFiles() {
        Filer filer = processingEnv.getFiler();
        for (String providerInterface : providers.keySet()) {
            String resourceFile = META_INF_OPTIMUS + providerInterface;
            log("Working on resource file: " + resourceFile);
            try {
                SortedSet<String> allServices = new TreeSet<>();
                try {
                    FileObject existingFile = filer.getResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                    log("Looking for existing resource file at " + existingFile.toUri());
                    Set<String> oldServices = OptimusFiles.readServiceFile(existingFile.openInputStream());
                    log("Existing service entries: " + oldServices);
                    allServices.addAll(oldServices);
                } catch (IOException e) {
                    log("Resource file did not already exist.");
                }
                String s = providers.get(providerInterface);
                Set<String> newServices = new HashSet<>();
                newServices.add(s);
                if (allServices.containsAll(newServices)) {
                    log("No new service entries being added.");
                    return;
                }

                allServices.addAll(newServices);
                log("New service file contents: " + allServices);
                FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);
                OutputStream out = fileObject.openOutputStream();
                OptimusFiles.writeServiceFile(allServices, out);
                out.close();
                log("Wrote to: " + fileObject.toUri());
            } catch (IOException e) {
                fatalError("Unable to create " + resourceFile + ", " + e);
                return;
            }
        }
    }


    private boolean checkExtendOptimusManager(TypeElement providerImplementer) {
        String verify = processingEnv.getOptions().get("verify");
        if (!Boolean.parseBoolean(verify)) {
            return true;
        }
        TypeMirror typeMirror = providerImplementer.getSuperclass();
        return "com.ghp.optimus.AbstractOptimusManager".equals(typeMirror.toString());
    }

    private boolean checkImplementer(TypeElement providerImplementer, TypeElement providerType) {
        String verify = processingEnv.getOptions().get("verify");
        if (!Boolean.parseBoolean(verify)) {
            return true;
        }
        Types types = processingEnv.getTypeUtils();
        return types.isSubtype(providerImplementer.asType(), providerType.asType());
    }


    private void log(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void error(String msg, Element element, AnnotationMirror annotation) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element, annotation);
    }

    private void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + msg);
    }

}
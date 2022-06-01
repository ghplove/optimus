package com.ghp.processor;


import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;


public class Utils {
    public static Map.Entry<ExecutableElement, AnnotationValue> getAnnotationElementAndValue(
            AnnotationMirror annotationMirror, final String elementName) {
        for (Map.Entry<ExecutableElement, AnnotationValue> entry :
                getAnnotationValuesWithDefaults(annotationMirror).entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(elementName)) {
                return entry;
            }
        }
        throw new IllegalArgumentException(String.format("@%s does not define an element %s()",
                ((TypeElement) (annotationMirror.getAnnotationType().asElement())).getQualifiedName(),
                elementName));
    }

    public static Map<ExecutableElement, AnnotationValue> getAnnotationValuesWithDefaults(
            AnnotationMirror annotation) {
        HashMap<ExecutableElement, AnnotationValue> values = new HashMap<>();
        Map<? extends ExecutableElement, ? extends AnnotationValue> declaredValues =
                annotation.getElementValues();
        for (ExecutableElement method :
                ElementFilter.methodsIn(annotation.getAnnotationType().asElement().getEnclosedElements())) {
            // Must iterate and put in this order, to ensure consistency in generated code.
            if (declaredValues.containsKey(method)) {
                values.put(method, declaredValues.get(method));
            } else if (method.getDefaultValue() != null) {
                values.put(method, method.getDefaultValue());
            } else {
                throw new IllegalStateException(
                        "Unset annotation value without default should never happen: "
                                + ((TypeElement) (method.getEnclosingElement())).getQualifiedName()
                                + '.' + method.getSimpleName() + "()");
            }
        }
        return values;
    }

    public static AnnotationMirror getAnnotationMirror(Element element,
                                                       Class<? extends Annotation> annotationClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            TypeElement annotationTypeElement = (TypeElement) annotationMirror.getAnnotationType().asElement();
            if (annotationTypeElement.getQualifiedName().contentEquals(annotationClassName)) {
                return annotationMirror;
            }
        }
        return null;
    }


    public static DeclaredType getValueField(AnnotationMirror annotationMirror) {
        if(annotationMirror != null){
            Map.Entry<ExecutableElement, AnnotationValue> entry = Utils.getAnnotationElementAndValue(annotationMirror, "value");
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                AnnotationValue annoValue = entry.getValue();
                return (DeclaredType) annoValue.getValue();
            }
        }
        return null;
    }

    /**
     * Returns the binary name of a reference type. For example,
     * {@code com.google.Foo$Bar}, instead of {@code com.google.Foo.Bar}.
     */
    public static String getBinaryName(TypeElement element) {
        return getBinaryNameImpl(element, element.getSimpleName().toString());
    }

    public static String getBinaryNameImpl(TypeElement element, String className) {
        Element enclosingElement = element.getEnclosingElement();

        if (enclosingElement instanceof PackageElement) {
            PackageElement pkg = (PackageElement) enclosingElement;
            if (pkg.isUnnamed()) {
                return className;
            }
            return pkg.getQualifiedName() + "." + className;
        }

        TypeElement typeElement = (TypeElement) enclosingElement;
        return getBinaryNameImpl(typeElement, typeElement.getSimpleName() + "$" + className);
    }


}

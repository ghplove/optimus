package com.ghp.optimus;

public interface InitCallback {

    int SUCCESS = 0;
    int ALREADY_INITIALED = 1;
    int CONTEXT_NULL = -1;
    int NOT_MAIN_PROGRESS = -2;
    int CLASS_NOT_FOUND = -4;
    int UNEXPECTED_EXCEPTION = -5;



    void onResult(int result);
}

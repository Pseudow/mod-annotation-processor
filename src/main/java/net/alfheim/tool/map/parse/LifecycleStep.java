package net.alfheim.tool.map.parse;

import net.alfheim.tool.map.exception.BuildProjectException;
import net.alfheim.tool.map.exception.ParseProjectException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

public interface LifecycleStep<T> {
    void transform(RoundEnvironment roundEnv) throws ParseProjectException;

    T process(ProcessingEnvironment processingEnv) throws BuildProjectException;
}

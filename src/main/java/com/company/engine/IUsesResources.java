package com.company.engine;

public interface IUsesResources {
    /**
     * For any Classes that use resources and mush clean up
     * once they are finished
     */
    void cleanUp();
}
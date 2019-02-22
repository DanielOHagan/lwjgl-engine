package com.company.engine.window;

public class WindowOptions {
    public boolean cullFace; //stops rendering faces that aren't in view (helps with performance)
    public boolean showFps; //display and fps counter
    public boolean compatibleProfile; //if OpenGL profile is supported
    public boolean antialiasing; //makes lines look more like lines, (decreases performance)
    public boolean frustrumCulling; //stops rendering objects outside of frustrum
    public boolean showMeshLines; //display mesh's lines NOTE: This breaks particles
    public boolean lockCursor; //keep the cursor locked to the window
    public boolean vSync; //enable vertical synchronisation
    public boolean fullscreen;
    public boolean resizable;
    public boolean displayAs2d; //tells the renderer to 'billboard' everything

    public int minWidth = 0;
    public int minHeight = 0;
    public int maxWidth = 0;
    public int maxHeight = 0;
}

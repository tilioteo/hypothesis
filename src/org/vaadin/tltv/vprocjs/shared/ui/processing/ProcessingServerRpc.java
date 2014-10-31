package org.vaadin.tltv.vprocjs.shared.ui.processing;

import com.vaadin.shared.communication.ServerRpc;

public interface ProcessingServerRpc extends ServerRpc {

    void click();

    void mousePress(int mouseX, int mouseY);

    void mouseRelease(int mouseX, int mouseY);

    void keyPress(int key);

    void keyRelease();

    void mouseEnter();

    void mouseLeave();

    void mouseWheel(boolean up, int deltaY);
}

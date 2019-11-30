package org.hypothesis.push;

import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

class PushUtility {

    static void forceManualPush(final UI ui) {
        if (PushMode.MANUAL.equals(ui.getPushConfiguration().getPushMode())) {
            try {
                ui.push();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

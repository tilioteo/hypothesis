package org.hypothesis.presenter;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by kamil on 02.11.16.
 */
public interface ExportThreadedService extends Serializable {

    void exportTests(Collection<Long> testIds);

    void requestCancel();
}

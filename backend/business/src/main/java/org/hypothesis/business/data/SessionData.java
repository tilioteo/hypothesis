package org.hypothesis.business.data;

import java.time.LocalDateTime;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
public class SessionData {

    private final String uid;
    private final TestStateData testStateData = new TestStateData();
    private String address;
    private String position;
    private LocalDateTime time;

    public void updateTime() {
        time = LocalDateTime.now();
    }

    public SessionData(String uid) {
        this.uid = uid;
        updateTime();
    }

    public String getUid() {
        return uid;
    }

    public TestStateData getTestStateData() {
        return testStateData;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        updateTime();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
        updateTime();
    }

    public LocalDateTime getTime() {
        return time;
    }

}

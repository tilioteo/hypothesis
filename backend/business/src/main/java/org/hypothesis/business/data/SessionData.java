package org.hypothesis.business.data;

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

    public SessionData(String uid) {
        this.uid = uid;
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
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}

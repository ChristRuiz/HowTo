package com.example.tfg;

public class Seguidor {

    private String uid_following;
    private String uid_follower;

    /**
     * Constructor
     * @param uid_following
     * @param uid_follower
     */
    public Seguidor(String uid_following, String uid_follower) {
        this.uid_following = uid_following;
        this.uid_follower = uid_follower;
    }

    /**
     * Getter de uid de following
     * @return
     */
    public String getUid_following() {
        return uid_following;
    }

    /**
     * Setter de uid de following
     * @param uid_following
     */
    public void setUid_following(String uid_following) {
        this.uid_following = uid_following;
    }

    /**
     * Getter de uid de follower
     * @return
     */
    public String getUid_follower() {
        return uid_follower;
    }

    /**
     * Setter de uid de follower
     * @param uid_follower
     */
    public void setUid_follower(String uid_follower) {
        this.uid_follower = uid_follower;
    }
}

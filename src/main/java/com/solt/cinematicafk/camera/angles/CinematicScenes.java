package com.solt.cinematicafk.camera.angles;

public enum CinematicScenes {
    SCENE_DRAMATIC_REVEAL(
        CinematicAngles.GROUND_CRAWL,
        CinematicAngles.BOTTOM_UP_REVEAL,
        CinematicAngles.ORBIT_CLOSE,
        CinematicAngles.BOOM_UP
    ),
    SCENE_ACTION_TRACKING(
        CinematicAngles.DOLLY_IN,
        CinematicAngles.DOLLY_RIGHT,
        CinematicAngles.ZIG_ZAG,
        CinematicAngles.FLY_BY
    ),
    SCENE_AERIAL_SURVEY(
        CinematicAngles.DIAGONAL_ASCENT,
        CinematicAngles.ORBIT_FAR,
        CinematicAngles.TOP_DOWN_SPIN,
        CinematicAngles.BIRDSEYE_DROP
    ),
    SCENE_SLOW_BUILDUP(
        CinematicAngles.ZOOM_OUT,
        CinematicAngles.SWING_LOW,
        CinematicAngles.STATIC_LOW_ANGLE,
        CinematicAngles.PENDULUM_SWING
    ),
    SCENE_CHAOTIC_ENERGY(
        CinematicAngles.WOBBLE_CAM,
        CinematicAngles.CORKSCREW_DROP,
        CinematicAngles.HOVER_CIRCLE,
        CinematicAngles.DIAGONAL_DESCENT
    ),
    SCENE_CINEMATIC_CLASSIC(
        CinematicAngles.PUSH_IN,
        CinematicAngles.OVER_THE_SHOULDER,
        CinematicAngles.DOLLY_LEFT,
        CinematicAngles.STATIC_HIGH_ANGLE
    ),
    SCENE_MYSTERIOUS_APPROACH(
        CinematicAngles.BACKWARD_RETREAT,
        CinematicAngles.FRONTAL_APPROACH,
        CinematicAngles.LOW_ANGLE_TRACKING,
        CinematicAngles.SPIRAL_REVEAL
    ),
    SCENE_EPIC_SCALE(
        CinematicAngles.BOOM_DOWN,
        CinematicAngles.PAN_AROUND,
        CinematicAngles.FLY_OVER,
        CinematicAngles.CORKSCREW_CLIMB
    ),
    SCENE_INTIMATE_FOCUS(
        CinematicAngles.STATIC_LOW_ANGLE,
        CinematicAngles.DOLLY_IN,
        CinematicAngles.ORBIT_CLOSE,
        CinematicAngles.SWING_LOW
    ),
    SCENE_VERTIGO(
        CinematicAngles.BIRDSEYE_DROP,
        CinematicAngles.SPIRAL_OUT,
        CinematicAngles.ZIG_ZAG,
        CinematicAngles.TOP_DOWN_SPIN
    );

    private final CinematicAngles[] sequence;

    CinematicScenes(CinematicAngles... sequence) {
        this.sequence = sequence;
    }

    public CinematicAngles[] getSequence() {
        return sequence;
    }
}

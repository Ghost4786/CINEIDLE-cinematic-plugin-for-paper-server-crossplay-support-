package com.solt.cinematicafk.camera.angles;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public enum CinematicAngles {
    ORBIT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 6.0;
            double angle = progress * Math.PI * 2;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), 1.5, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DRONE_DROP {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double height = 15.0 - (progress * 13.0);
            Location loc = playerLoc.clone().add(5.0 * Math.cos(progress * Math.PI / 2), height, 5.0 * Math.sin(progress * Math.PI / 2));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    GROUND_PAN {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Location loc = playerLoc.clone().add(8.0 - (progress * 16.0), 0.2, 8.0 - (progress * 16.0));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DUTCH_ANGLE_ORBIT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 5.5;
            double angle = progress * Math.PI * 2;
            double height = 1.5 + Math.sin(progress * Math.PI * 4) * 2.0; 
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), height, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    CRANE_SHOT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 2.0 + progress * 15.0;
            double height = 0.5 + progress * 12.0;
            Location loc = playerLoc.clone().add(dist * Math.cos(Math.PI / 4), height, dist * Math.sin(Math.PI / 4));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    OVER_THE_SHOULDER {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0).normalize();
            double dist = -6.0 + (progress * 4.0);
            Location loc = playerLoc.clone().add(dir.clone().multiply(dist));
            Vector right = new Vector(-dir.getZ(), 0, dir.getX()).normalize().multiply(1.5);
            loc.add(right).add(0, 2.0, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    HELIX {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 3.0 + (progress * 5.0);
            double angle = progress * Math.PI * 6;
            double height = progress * 15.0;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), height, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    WIDE_ESTABLISHING {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 25.0 - (progress * 10.0);
            Location loc = playerLoc.clone().add(dist, 10.0, dist);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    PUSH_IN {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 20.0 - (progress * 15.0);
            Location loc = playerLoc.clone().add(dist * Math.cos(Math.PI / 6), 2.0, dist * Math.sin(Math.PI / 6));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    FLY_BY {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double xOffset = -15.0 + (progress * 30.0); 
            Location loc = playerLoc.clone().add(xOffset, 3.0, 5.0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    SPIRAL_REVEAL {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 2.0 + (progress * 8.0);
            double angle = progress * Math.PI * 4; 
            double height = -1.0 + (progress * 12.0); 
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), height, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    LOW_ANGLE_TRACKING {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 7.0;
            double angle = Math.PI + (progress * Math.PI); 
            double height = 0.0; 
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), height, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    ZOOM_OUT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 2.0 + (progress * 20.0);
            Location loc = playerLoc.clone().add(dist * Math.cos(Math.PI / 4), 2.0, dist * Math.sin(Math.PI / 4));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DOLLY_LEFT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            Vector right = new Vector(-dir.getZ(), 0, dir.getX()).normalize();
            double offset = 10.0 - (progress * 20.0);
            Location loc = playerLoc.clone().add(right.multiply(offset)).add(0, 2.0, 0).add(dir.multiply(8.0));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DOLLY_RIGHT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            Vector right = new Vector(-dir.getZ(), 0, dir.getX()).normalize();
            double offset = -10.0 + (progress * 20.0);
            Location loc = playerLoc.clone().add(right.multiply(offset)).add(0, 2.0, 0).add(dir.multiply(8.0));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    TOP_DOWN_SPIN {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 1.0;
            double angle = progress * Math.PI * 4;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), 15.0, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    BOTTOM_UP_REVEAL {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double height = -2.0 + (progress * 10.0);
            Location loc = playerLoc.clone().add(5.0, height, 5.0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    STATIC_HIGH_ANGLE {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Location loc = playerLoc.clone().add(8.0, 12.0, 8.0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    STATIC_LOW_ANGLE {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Location loc = playerLoc.clone().add(5.0, -1.0, 5.0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    ZIG_ZAG {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double x = 5.0 * Math.sin(progress * Math.PI * 6);
            double z = 10.0 - (progress * 20.0);
            Location loc = playerLoc.clone().add(x, 3.0, z);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    FRONTAL_APPROACH {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            double dist = 25.0 - (progress * 20.0);
            Location loc = playerLoc.clone().add(dir.multiply(dist)).add(0, 1.5, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    BACKWARD_RETREAT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            double dist = -2.0 - (progress * 15.0);
            Location loc = playerLoc.clone().add(dir.multiply(dist)).add(0, 1.5, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    PENDULUM_SWING {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double angle = Math.sin(progress * Math.PI * 2) * (Math.PI / 4);
            double radius = 10.0;
            Location loc = playerLoc.clone().add(radius * Math.sin(angle), 5.0, radius * Math.cos(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    CORKSCREW_DROP {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 4.0;
            double angle = progress * Math.PI * 8;
            double height = 20.0 - (progress * 18.0);
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), height, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    HOVER_CIRCLE {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 8.0;
            double angle = progress * Math.PI * 2;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), 8.0, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DIAGONAL_ASCENT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 2.0 + (progress * 15.0);
            double height = progress * 15.0;
            Location loc = playerLoc.clone().add(dist, height, dist);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    WOBBLE_CAM {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dx = Math.sin(progress * Math.PI * 10) * 1.5;
            double dy = Math.cos(progress * Math.PI * 13) * 1.5;
            double dz = Math.sin(progress * Math.PI * 7) * 1.5;
            Location loc = playerLoc.clone().add(5.0 + dx, 2.0 + dy, 5.0 + dz);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    BIRDSEYE_DROP {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double height = 30.0 - (progress * 25.0);
            Location loc = playerLoc.clone().add(0, height, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    GROUND_CRAWL {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 15.0 - (progress * 10.0);
            Location loc = playerLoc.clone().add(dist * Math.cos(progress * Math.PI), -0.5, dist * Math.sin(progress * Math.PI));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    ORBIT_FAR {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 15.0;
            double angle = progress * Math.PI * 2;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), 4.0, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    SWING_LOW {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double angle = Math.PI + (progress * Math.PI);
            double height = -1.0 + Math.sin(progress * Math.PI) * 4.0;
            Location loc = playerLoc.clone().add(8.0 * Math.cos(angle), height, 8.0 * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    FLY_OVER {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            double dist = -10.0 + (progress * 20.0);
            double height = 8.0 - (Math.abs(dist) * 0.5);
            Location loc = playerLoc.clone().add(dir.multiply(dist)).add(0, height, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    BOOM_UP {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            double height = progress * 15.0;
            Location loc = playerLoc.clone().add(dir.multiply(10.0)).add(0, height, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    BOOM_DOWN {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            double height = 15.0 - (progress * 15.0);
            Location loc = playerLoc.clone().add(dir.multiply(10.0)).add(0, height, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DIAGONAL_DESCENT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 15.0 - (progress * 10.0);
            double height = 15.0 - (progress * 13.0);
            Location loc = playerLoc.clone().add(dist, height, dist);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    ORBIT_CLOSE {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 3.0;
            double angle = progress * Math.PI * 4;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), 1.5, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    DOLLY_IN {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double dist = 25.0 - (progress * 22.0);
            Location loc = playerLoc.clone().add(dist * Math.cos(Math.PI / 4), 1.5, dist * Math.sin(Math.PI / 4));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    SPIRAL_OUT {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 2.0 + (progress * 15.0);
            double angle = progress * Math.PI * 6;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), 5.0, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    CORKSCREW_CLIMB {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            double radius = 10.0 - (progress * 7.0);
            double angle = progress * Math.PI * 8;
            double height = progress * 20.0;
            Location loc = playerLoc.clone().add(radius * Math.cos(angle), height, radius * Math.sin(angle));
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    },
    PAN_AROUND {
        @Override
        public Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult) {
            Vector dir = playerLoc.getDirection().setY(0);
            if (dir.lengthSquared() < 0.0001) dir = new Vector(1, 0, 0); else dir.normalize();
            double angleOffset = (progress * Math.PI) - (Math.PI / 2);
            double radius = 12.0;
            Vector offset = new Vector(
                dir.getX() * Math.cos(angleOffset) - dir.getZ() * Math.sin(angleOffset),
                0,
                dir.getX() * Math.sin(angleOffset) + dir.getZ() * Math.cos(angleOffset)
            ).multiply(radius);
            Location loc = playerLoc.clone().add(offset).add(0, 3.0, 0);
            return applyRaytraceAndLook(playerEyeLoc, loc, distMult);
        }
    };

    public abstract Location getCameraLocation(Location playerLoc, Location playerEyeLoc, double progress, double distMult);

    /**
     * Executes a fast mathematical raytrace from the player's eye to the target camera location.
     * Guaranteed to be running synchronously safely inside the CameraManager task.
     * Calculates precise mathematical trigonometric Pitch and Yaw targeting the player's head.
     */
    protected static Location applyRaytraceAndLook(Location playerEye, Location targetLoc, double distanceMultiplier) {
        Vector offset = targetLoc.toVector().subtract(playerEye.toVector());
        offset.multiply(distanceMultiplier);
        targetLoc = playerEye.clone().add(offset);
        
        Vector direction = targetLoc.toVector().subtract(playerEye.toVector());
        double distance = direction.length();
        
        if (distance > 0.01) {
            direction.normalize();
            
            // Raytrace safely handled synchronously in CameraManager loop
            RayTraceResult result = playerEye.getWorld().rayTraceBlocks(
                    playerEye, direction, distance, FluidCollisionMode.NEVER, true);
                    
            if (result != null && result.getHitBlock() != null) {
                double clampDistance = Math.max(0.0, result.getHitPosition().distance(playerEye.toVector()) - 0.5);
                targetLoc = playerEye.clone().add(direction.multiply(clampDistance));
            }
        }
        
        // Exact trigonometric Look-At calculation: Target (Player Eye) - Origin (Camera Location)
        Vector lookDirection = playerEye.toVector().subtract(targetLoc.toVector());
        
        // Zero-check to prevent Vector direction exceptions
        if (lookDirection.lengthSquared() > 0.0001) {
            targetLoc.setDirection(lookDirection);
        }
        
        return targetLoc;
    }
}

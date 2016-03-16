package us.mcmagic.parkmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.TrigMath;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Random;

public class MathUtil {
    public static double trim(int degree, double d) {
        String format = "#.#";

        for (int i = 1; i < degree; i++)
            format += "#";

        DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.valueOf(twoDForm.format(d));
    }


    public static Random random = new Random();

    public static int r(int i) {
        return random.nextInt(i);
    }

    public static double offset2d(Entity a, Entity b) {
        return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset2d(Location a, Location b) {
        return offset2d(a.toVector(), b.toVector());
    }

    public static double offset2d(Vector a, Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }

    public static double offset(Entity a, Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset(Location a, Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static double offset(Vector a, Vector b) {
        return a.subtract(b).length();
    }

    private static final int CHUNK_BITS = 4;
    private static final int CHUNK_VALUES = 16;
    public static final float DEGTORAD = 0.01745329F;
    public static final float RADTODEG = 57.29578F;
    public static final double HALFROOTOFTWO = 0.707106781D;

    public static double lengthSquared(double[] values) {
        double rval = 0.0D;
        for (double value : values) {
            rval += value * value;
        }
        return rval;
    }

    public static double length(double[] values) {
        return Math.sqrt(lengthSquared(values));
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return length(new double[]{x1 - x2, y1 - y2});
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return lengthSquared(new double[]{x1 - x2, y1 - y2});
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return length(new double[]{x1 - x2, y1 - y2, z1 - z2});
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return lengthSquared(new double[]{x1 - x2, y1 - y2, z1 - z2});
    }

    public static double getPercentage(int subtotal, int total, int decimals) {
        return round(getPercentage(subtotal, total), decimals);
    }

    public static double getPercentage(int subtotal, int total) {
        return subtotal / total * 100.0F;
    }

    public static int getAngleDifference(int angle1, int angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    public static float getAngleDifference(float angle1, float angle2) {
        return Math.abs(wrapAngle(angle1 - angle2));
    }

    public static int wrapAngle(int angle) {
        int wrappedAngle = angle;
        while (wrappedAngle <= -180) {
            wrappedAngle += 360;
        }
        while (wrappedAngle > 180) {
            wrappedAngle -= 360;
        }
        return wrappedAngle;
    }

    public static float wrapAngle(float angle) {
        float wrappedAngle = angle;
        while (wrappedAngle <= -180.0F) {
            wrappedAngle += 360.0F;
        }
        while (wrappedAngle > 180.0F) {
            wrappedAngle -= 360.0F;
        }
        return wrappedAngle;
    }

    public static double normalize(double x, double z, double reqx, double reqz) {
        return Math.sqrt(lengthSquared(new double[]{reqx, reqz}) / lengthSquared(new double[]{x, z}));
    }

    public static float getLookAtYaw(Entity loc, Entity lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Block loc, Block lookat) {
        return getLookAtYaw(loc.getLocation(), lookat.getLocation());
    }

    public static float getLookAtYaw(Location loc, Location lookat) {
        return getLookAtYaw(lookat.getX() - loc.getX(), lookat.getZ() - loc.getZ());
    }

    public static float getLookAtYaw(Vector motion) {
        return getLookAtYaw(motion.getX(), motion.getZ());
    }

    public static float getLookAtYaw(double dx, double dz) {
        float amount = atan2(dz, dx) - 90.0F;
        Bukkit.broadcastMessage("x: " + dx + " z: " + dz + " amount: " + String.valueOf(amount));
        return amount;
    }

    public static float getLookAtPitch(double dX, double dY, double dZ) {
        return getLookAtPitch(dY, length(new double[]{dX, dZ}));
    }

    public static float getLookAtPitch(double dY, double dXZ) {
        return -atan(dY / dXZ);
    }

    public static float atan(double value) {
        return 57.29578F * (float) TrigMath.atan(value);
    }

    public static float atan2(double y, double x) {
        return 57.29578F * (float) TrigMath.atan2(y, x);
    }

    public static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    public static int ceil(double value) {
        return -floor(-value);
    }

    public static int ceil(float value) {
        return -floor(-value);
    }

    public static Location move(Location loc, Vector offset) {
        return move(loc, offset.getX(), offset.getY(), offset.getZ());
    }

    public static Location move(Location loc, double dx, double dy, double dz) {
        Vector off = rotate(loc.getYaw(), loc.getPitch(), dx, dy, dz);
        double x = loc.getX() + off.getX();
        double y = loc.getY() + off.getY();
        double z = loc.getZ() + off.getZ();
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    public static Vector rotate(float yaw, float pitch, Vector vector) {
        return rotate(yaw, pitch, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector rotate(float yaw, float pitch, double x, double y, double z) {
        float angle = yaw * 0.01745329F;
        double sinyaw = Math.sin(angle);
        double cosyaw = Math.cos(angle);

        angle = pitch * 0.01745329F;
        double sinpitch = Math.sin(angle);
        double cospitch = Math.cos(angle);

        Vector vector = new Vector();
        vector.setX(x * sinyaw - y * cosyaw * sinpitch - z * cosyaw * cospitch);
        vector.setY(y * cospitch - z * sinpitch);
        vector.setZ(-(x * cosyaw) - y * sinyaw * sinpitch - z * sinyaw * cospitch);
        return vector;
    }

    public static double round(double value, int decimals) {
        double p = Math.pow(10.0D, decimals);
        return Math.round(value * p) / p;
    }

    public static double fixNaN(double value) {
        return fixNaN(value, 0.0D);
    }

    public static double fixNaN(double value, double def) {
        return Double.isNaN(value) ? def : value;
    }

    public static int toChunk(double loc) {
        return floor(loc / 16.0D);
    }

    public static int toChunk(int loc) {
        return loc >> 4;
    }

    public static double useOld(double oldvalue, double newvalue, double peruseold) {
        return oldvalue + peruseold * (newvalue - oldvalue);
    }

    public static double lerp(double d1, double d2, double stage) {
        if ((Double.isNaN(stage)) || (stage > 1.0D))
            return d2;
        if (stage < 0.0D) {
            return d1;
        }
        return d1 * (1.0D - stage) + d2 * stage;
    }

    public static Vector lerp(Vector vec1, Vector vec2, double stage) {
        Vector newvec = new Vector();
        newvec.setX(lerp(vec1.getX(), vec2.getX(), stage));
        newvec.setY(lerp(vec1.getY(), vec2.getY(), stage));
        newvec.setZ(lerp(vec1.getZ(), vec2.getZ(), stage));
        return newvec;
    }

    public static Location lerp(Location loc1, Location loc2, double stage) {
        Location newloc = new Location(loc1.getWorld(), 0.0D, 0.0D, 0.0D);
        newloc.setX(lerp(loc1.getX(), loc2.getX(), stage));
        newloc.setY(lerp(loc1.getY(), loc2.getY(), stage));
        newloc.setZ(lerp(loc1.getZ(), loc2.getZ(), stage));
        newloc.setYaw((float) lerp(loc1.getYaw(), loc2.getYaw(), stage));
        newloc.setPitch((float) lerp(loc1.getPitch(), loc2.getPitch(), stage));
        return newloc;
    }

    public static boolean isInverted(double value1, double value2) {
        return ((value1 > 0.0D) && (value2 < 0.0D)) || ((value1 < 0.0D) && (value2 > 0.0D));
    }

    public static Vector getDirection(float yaw, float pitch) {
        Vector vector = new Vector();
        double rotX = 0.01745329F * yaw;
        double rotY = 0.01745329F * pitch;
        vector.setY(-Math.sin(rotY));
        double h = Math.cos(rotY);
        vector.setX(-h * Math.sin(rotX));
        vector.setZ(h * Math.cos(rotX));
        return vector;
    }

    public static double clamp(double value, double limit) {
        return clamp(value, -limit, limit);
    }

    public static double clamp(double value, double min, double max) {
        return value > max ? max : value < min ? min : value;
    }

    public static float clamp(float value, float limit) {
        return clamp(value, -limit, limit);
    }

    public static float clamp(float value, float min, float max) {
        return value > max ? max : value < min ? min : value;
    }

    public static int clamp(int value, int limit) {
        return clamp(value, -limit, limit);
    }

    public static int clamp(int value, int min, int max) {
        return value > max ? max : value < min ? min : value;
    }

    public static int invert(int value, boolean negative) {
        return negative ? -value : value;
    }

    public static float invert(float value, boolean negative) {
        return negative ? -value : value;
    }

    public static double invert(double value, boolean negative) {
        return negative ? -value : value;
    }

    public static long toLong(int msw, int lsw) {
        return longHashToLong(msw, lsw);
    }

    public static long longHashToLong(int msw, int lsw) {
        return LongHash.toLong(msw, lsw);
    }

    public static int longHashMsw(long key) {
        return LongHash.msw(key);
    }

    public static int longHashLsw(long key) {
        return LongHash.lsw(key);
    }

    public static void setVectorLength(Vector vector, double length) {
        setVectorLengthSquared(vector, Math.signum(length) * length * length);
    }

    public static void setVectorLengthSquared(Vector vector, double lengthsquared) {
        double vlength = vector.lengthSquared();
        if (Math.abs(vlength) > 0.0001D)
            if (lengthsquared < 0.0D)
                vector.multiply(-Math.sqrt(-lengthsquared / vlength));
            else
                vector.multiply(Math.sqrt(lengthsquared / vlength));
    }

    public static boolean isHeadingTo(BlockFace direction, Vector velocity) {
        return isHeadingTo(FaceUtil.faceToVector(direction), velocity);
    }

    public static boolean isHeadingTo(Location from, Location to, Vector velocity) {
        return isHeadingTo(new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()), velocity);
    }

    public static boolean isHeadingTo(Vector offset, Vector velocity) {
        double dbefore = offset.lengthSquared();
        if (dbefore < 0.0001D) {
            return true;
        }
        Vector clonedVelocity = velocity.clone();
        setVectorLengthSquared(clonedVelocity, dbefore);
        return dbefore > clonedVelocity.subtract(offset).lengthSquared();
    }
}
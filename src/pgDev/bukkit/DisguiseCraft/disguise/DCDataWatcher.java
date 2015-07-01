package pgDev.bukkit.DisguiseCraft.disguise;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject;
import org.apache.commons.lang3.ObjectUtils;

public class DCDataWatcher extends DataWatcher {
	static Method getWatchableMethod;
	static Field eBoolean;
	
	static {
		try {
			getWatchableMethod = DataWatcher.class.getDeclaredMethod("j", int.class);
			getWatchableMethod.setAccessible(true);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not find a required method in the DataWatchers",e);
		}
		
		try {
			eBoolean = DataWatcher.class.getDeclaredField("e");
			eBoolean.setAccessible(true);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not find a required boolean field in the DataWatchers",e);
		}
	}

	public DCDataWatcher() {
		super(null);
	}
	
	@Override
	public void watch(int paramInt, Object paramObject) {
		WatchableObject localWatchableObject = null;
		try {
			localWatchableObject = (WatchableObject) getWatchableMethod.invoke(this, paramInt);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Error while invoking method in a DataWatcher", e);
		}
		
		if (ObjectUtils.notEqual(paramObject, localWatchableObject.b())) {
			localWatchableObject.a(paramObject);
			localWatchableObject.a(true);
			try {
				eBoolean.setBoolean(this, true);
			} catch (Exception e) {
				DisguiseCraft.logger.log(Level.SEVERE, "Error while setting a boolean in a DataWatcher", e);
			}
		}
	}
}

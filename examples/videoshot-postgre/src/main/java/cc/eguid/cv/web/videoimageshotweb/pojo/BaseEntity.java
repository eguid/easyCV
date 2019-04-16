package cc.eguid.cv.web.videoimageshotweb.pojo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 重写toString方法
	 */
	@Override
	public String toString() {
		return toString(this);
	}
	
	/**
	 * toString方法
	 * @param obj
	 * @return
	 */
	public final static String toString(Object obj) {
		Class<?> cla = obj.getClass();
		StringBuilder sb = new StringBuilder(cla.getSimpleName());
		Field[] fields = cla.getDeclaredFields();
		sb.append(" [");
		int num=0;
		for(Field field :fields) {
			boolean isFinalOrStatic = Modifier.isStatic(field.getModifiers());
			//|| Modifier.isFinal(fields[i].getModifiers());
			if (isFinalOrStatic) {
				continue;
			}else if (num > 0) {
				sb.append(", ");
			}
			num++;
			sb.append(field.getName()).append("=");
			try {
				field.setAccessible(true);
				sb.append(field.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		sb.append("]");
		return sb.toString();
	}
}

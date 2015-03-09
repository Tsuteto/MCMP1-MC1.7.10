package tsuteto.mcmp.core.util;

import java.lang.reflect.Field;

public class ReflectionUtil
{
    public static Field getFieldMatchingType(Class<?> clazz, Class<?> type)
    {
        Field ret = null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            if (type.isAssignableFrom(field.getType()))
            {
                if (ret != null)
                {
                    return null;
                }

                field.setAccessible(true);
                ret = field;
            }
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValueMatchingType(Object object, Class<T> type)
    {
        Field field = getFieldMatchingType(object.getClass(), type);
        if (field == null)
        {
            return null;
        }
        else
        {
            try
            {
                return (T)field.get(object);
            }
            catch (Exception var4)
            {
                throw new RuntimeException(var4);
            }
        }
    }
}

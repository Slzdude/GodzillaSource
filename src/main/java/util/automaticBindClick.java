package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class automaticBindClick {
    public automaticBindClick() {
    }

    public static void bindButtonClick(final Object fieldClass, Object eventClass) {
        try {
            Field[] fields = fieldClass.getClass().getDeclaredFields();
            int var5 = fields.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Field field = fields[var4];
                if (field.getType().isAssignableFrom(Button.class)) {
                    field.setAccessible(true);
                    Button fieldValue = (Button) field.get(fieldClass);
                    String fieldName = field.getName();
                    if (fieldValue != null) {
                        try {
                            if (fieldName.equals("selectdFileButton")) {
                                System.out.println();
                            }

                            final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                            method.setAccessible(true);
                            fieldValue.addActionListener(e -> {
                                try {
                                    method.invoke(fieldClass, e);
                                } catch (Exception var3) {
                                    Log.error(var3);
                                }

                            });
                        } catch (NoSuchMethodException var10) {
                            System.out.println(fieldName + "Click" + "  未实现");
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }

    public static void bindJButtonClick(final Object fieldClass, Object eventClass) {
        try {
            Field[] fields = fieldClass.getClass().getDeclaredFields();
            int var5 = fields.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Field field = fields[var4];
                if (field.getType().isAssignableFrom(JButton.class)) {
                    field.setAccessible(true);
                    JButton fieldValue = (JButton) field.get(fieldClass);
                    String fieldName = field.getName();
                    if (fieldValue != null) {
                        try {
                            final Method method = eventClass.getClass().getDeclaredMethod(fieldName + "Click", ActionEvent.class);
                            method.setAccessible(true);
                            fieldValue.addActionListener(e -> {
                                try {
                                    method.invoke(fieldClass, e);
                                } catch (Exception var3) {
                                    var3.printStackTrace();
                                }

                            });
                        } catch (NoSuchMethodException var10) {
                            Log.error(fieldName + "Click" + "  未实现");
                        }
                    }
                }
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

    }

    public static void bindMenuItemClick(Object item, Map methodMap, Object eventClass) {
        MenuElement[] menuElements = ((MenuElement) item).getSubElements();
        if (methodMap == null) {
            methodMap = getMenuItemMethod(eventClass);
        }

        if (menuElements.length == 0) {
            if (item.getClass().isAssignableFrom(JMenuItem.class)) {
                Method method = (Method) methodMap.get(((JMenuItem) item).getActionCommand() + "MenuItemClick");
                addMenuItemClickEvent(item, method, eventClass);
            }
        } else {
            for (MenuElement menuElement : menuElements) {
                Class itemClass = menuElement.getClass();
                if (!itemClass.isAssignableFrom(JPopupMenu.class) && !itemClass.isAssignableFrom(JMenu.class)) {
                    if (item.getClass().isAssignableFrom(JMenuItem.class)) {
                        Method method = (Method) methodMap.get(((JMenuItem) menuElement).getActionCommand() + "MenuItemClick");
                        addMenuItemClickEvent(menuElement, method, eventClass);
                    }
                } else {
                    bindMenuItemClick(menuElement, methodMap, eventClass);
                }
            }
        }

    }

    private static Map getMenuItemMethod(Object eventClass) {
        Method[] methods = eventClass.getClass().getDeclaredMethods();
        Map methodMap = new HashMap();

        for (Method method : methods) {
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(ActionEvent.class) && method.getReturnType().isAssignableFrom(Void.TYPE) && method.getName().endsWith("MenuItemClick")) {
                methodMap.put(method.getName(), method);
            }
        }

        return methodMap;
    }

    private static void addMenuItemClickEvent(Object item, final Method method, final Object eventClass) {
        if (method != null && eventClass != null && item.getClass().isAssignableFrom(JMenuItem.class)) {
            ((JMenuItem) item).addActionListener(paramActionEvent -> {
                try {
                    method.setAccessible(true);
                    method.invoke(eventClass, paramActionEvent);
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

            });
        }

    }
}

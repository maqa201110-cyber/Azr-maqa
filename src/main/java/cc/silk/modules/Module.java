package cc.silk.modules;

import cc.silk.Silk;
import cc.silk.api.config.Serializable;
import cc.silk.api.notifications.NotificationManager;
import cc.silk.api.notifications.NotificationType;
import cc.silk.api.properties.Property;
import cc.silk.api.properties.impl.ModeProperty;
import cc.silk.api.properties.impl.MultiModeProperty;
import cc.silk.api.properties.impl.NumberProperty;
import cc.silk.modules.impl.client.ToggleSoundsModule;
import cc.silk.modules.impl.visuals.NotificationsModule;
import cc.silk.utils.client.SoundUtils;
import cc.silk.utils.misc.Manager;
import cc.silk.utils.render.Translate;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Field;
import java.util.List;

public class Module extends Manager<Property<?>> implements Toggleable, Serializable {

    private final String label = getClass().getAnnotation(ModuleInfo.class).label();
    private final String description = getClass().getAnnotation(ModuleInfo.class).description();
    private final ModuleCategory category = getClass().getAnnotation(ModuleInfo.class).category();
    private int key = getClass().getAnnotation(ModuleInfo.class).key();
    private boolean enabled;
    private boolean hidden;
    @Getter
    @Setter
    private String suffix;
    private final Translate translate = new Translate(0.0, 0.0);

    public void resetPropertyValues() {
        for (Property<?> property : getElements())
            property.callFirstTime();
    }

    public Translate getTranslate() {
        return translate;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public void reflectProperties() {
        for (final Field field : getClass().getDeclaredFields()) {
            final Class<?> type = field.getType();
            if (type.isAssignableFrom(Property.class) ||
                    type.isAssignableFrom(NumberProperty.class) ||
                    type.isAssignableFrom(ModeProperty.class) ||
                    type.isAssignableFrom(MultiModeProperty.class)) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    elements.add((Property<?>) field.get(this));
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;

            if (enabled) {
                onEnable();
                Silk.INSTANCE.getEventBus().subscribe(this);
            } else {
                Silk.INSTANCE.getEventBus().unsubscribe(this);
                onDisable();
            }
        }
    }

    public boolean isVisible() {
        return enabled && !hidden;
    }

    @Override
    public void toggle() {
        setEnabled(!enabled);
        if (MinecraftClient.getInstance().player != null) {
            if (Silk.INSTANCE.getModuleManager().getModule(NotificationsModule.class).isEnabled()) {
                String titleToggle = "Module toggled";
                String descriptionToggleOn = this.getLabel() + " was " + "§aenabled!";
                String descriptionToggleOff = this.getLabel() + " was " + "§cdisabled!";

                if (enabled) {
                    NotificationManager.post(NotificationType.SUCCESS, titleToggle, descriptionToggleOn);
                } else {
                    NotificationManager.post(NotificationType.DISABLE, titleToggle, descriptionToggleOff);
                }
            }
            if (Silk.INSTANCE.getModuleManager().getModule(ToggleSoundsModule.class).isEnabled()) {
                switch (ToggleSoundsModule.mode.getValue()) {
                    case Silk:
                        if (enabled) {
                            SoundUtils.playSound("silk-enable.wav");
                        } else {
                            SoundUtils.playSound("silk-disable.wav");
                        }
                        break;
                    case Augustus:
                        if (enabled) {
                            SoundUtils.playSound("augustus-enable.wav");
                        } else {
                            SoundUtils.playSound("augustus-disable.wav");
                        }
                        break;
                    case Vanilla:
                        SoundUtils.playSound("minecraft-toggle.wav");
                        break;
                    case Sigma5:
                        if (enabled) {
                            SoundUtils.playSound("sigma5-enable.wav");
                        } else {
                            SoundUtils.playSound("sigma5-disable.wav");
                        }
                        break;
                    case Note:
                        if (enabled) {
                            SoundUtils.playSound("note-enable.wav");
                        } else {
                            SoundUtils.playSound("note-disable.wav");
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public JsonObject save() {
        return save(true);
    }

    public JsonObject save(boolean saveKey) {
        JsonObject object = new JsonObject();
        object.addProperty("toggled", isEnabled());
        if (saveKey) {
            object.addProperty("key", getKey());
        }
        object.addProperty("hidden", isHidden());
        List<Property<?>> properties = getElements();
        if (!properties.isEmpty()) {
            JsonObject propertiesObject = new JsonObject();

            for (Property<?> property : properties) {
                if (property instanceof NumberProperty) {
                    propertiesObject.addProperty(property.getLabel(), ((NumberProperty) property).getValue());
                } else if (property instanceof ModeProperty) {
                    ModeProperty<?> ModeProperty = (ModeProperty<?>) property;
                    propertiesObject.add(property.getLabel(), new JsonPrimitive(ModeProperty.getValue().name()));
                } else if (property instanceof MultiModeProperty) {
                    MultiModeProperty<?> multiSelect = (MultiModeProperty<?>) property;
                    final JsonArray array = new JsonArray();
                    for (Enum<?> e : multiSelect.getValues()) {
                        array.add(new JsonPrimitive(e.name()));
                    }
                    propertiesObject.add(property.getLabel(), array);
                } else if (property.getType() == Boolean.class) {
                    propertiesObject.addProperty(property.getLabel(), (Boolean) property.getValue());
                } else if (property.getType() == Integer.class) {
                    propertiesObject.addProperty(property.getLabel(), Integer.toHexString((Integer) property.getValue()));
                } else if (property.getType() == String.class) {
                    propertiesObject.addProperty(property.getLabel(), (String) property.getValue());
                }
            }

            object.add("Properties", propertiesObject);
        }
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(JsonObject object) {
        load(object, false);
    }

    public void load(JsonObject object, boolean loadKey) {
        if (object.has("toggled"))
            setEnabled(object.get("toggled").getAsBoolean());

        if (loadKey && object.has("key"))
            setKey(object.get("key").getAsInt());

        if (object.has("hidden"))
            setHidden(object.get("hidden").getAsBoolean());

        if (object.has("Properties") && !getElements().isEmpty()) {
            JsonObject propertiesObject = object.getAsJsonObject("Properties");
            for (Property<?> property : getElements()) {
                if (propertiesObject.has(property.getLabel())) {
                    if (property instanceof NumberProperty) {
                        ((NumberProperty) property).setValue(propertiesObject.get(property.getLabel()).getAsDouble());
                    } else if (property instanceof ModeProperty) {
                        findEnumValue(property, propertiesObject);
                    } else if (property instanceof MultiModeProperty) {

                    } else if (property.getValue() instanceof Boolean) {
                        ((Property<Boolean>) property).setValue(propertiesObject.get(property.getLabel()).getAsBoolean());
                    } else if (property.getValue() instanceof Integer) {
                        ((Property<Integer>) property).setValue((int) Long.parseLong(propertiesObject.get(property.getLabel()).getAsString(), 16));
                    } else if (property.getValue() instanceof String) {
                        ((Property<String>) property).setValue(propertiesObject.get(property.getLabel()).getAsString());
                    }
                }
            }
        }
    }


    private static <T extends Enum<T>> void findEnumValue(Property<?> property, JsonObject propertiesObject) {
        ModeProperty<T> ModeProperty = (ModeProperty<T>) property;
        String value = propertiesObject.getAsJsonPrimitive(property.getLabel()).getAsString();
        for (T possibleValue : ModeProperty.getValues()) {
            if (possibleValue.name().equalsIgnoreCase(value)) {
                ModeProperty.setValue(possibleValue);
                break;
            }
        }
    }
}

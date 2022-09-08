package com.example.iot2;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.DeviceTypes;
import android.service.controls.actions.BooleanAction;
import android.service.controls.actions.CommandAction;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.FloatAction;
import android.service.controls.templates.ControlButton;
import android.service.controls.templates.ControlTemplate;
import android.service.controls.templates.RangeTemplate;
import android.service.controls.templates.StatelessTemplate;
import android.service.controls.templates.ToggleRangeTemplate;
import android.service.controls.templates.ToggleTemplate;

import com.example.iot2.colorpicker.Color;
import com.example.iot2.dataclasses.AirFresh;
import com.example.iot2.dataclasses.Device;
import com.example.iot2.dataclasses.Devices;
import com.example.iot2.dataclasses.Light;
import com.example.iot2.dataclasses.MyDeviceTypes;
import com.example.iot2.dataclasses.Scenario;
import com.example.iot2.dataclasses.Switch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.reactivestreams.FlowAdapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Iot extends ControlsProviderService {
    public static final String HOST = "http://192.168.86.195:8000/";
    public static final String SUBTITLE = "Mathew's room";
    public static final String STRUCTURE = "St. Petersburg";
    List<String> controlIds;
    public Map<String, Device> devices = new HashMap<>();
    Map<String, Scenario> scenarioMap = new HashMap<>();
    Color[] colors;

    protected static Devices _getDevices() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        OkHttpClient client = new OkHttpClient();
        String url = HOST + "devices";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return mapper.readValue(Objects.requireNonNull(response.body()).string(), Devices.class);
        }
    }

    public static Devices getDevices() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicBoolean state = new AtomicBoolean(false);
        AtomicReference<Devices> devices = new AtomicReference<>();
        executor.execute(() -> {
            try {
                Devices devices1 = _getDevices();
                System.out.println(devices1);
                devices.set(devices1);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                state.set(true);
            }
        });
        while (!state.get()) ;
        return devices.get();
    }


    private Control createStatelessControl(String name, int deviceType, String userFriendlyName) {
        Context context = getBaseContext();
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        Control control = new Control.StatelessBuilder(name, pi)
                .setTitle(userFriendlyName)
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setDeviceType(deviceType)
                .build();
        return control;
    }

    private int createDevice(Device device) {
        int deviceType;

        if (device.getType().equals(MyDeviceTypes.SWITCH)) deviceType = DeviceTypes.TYPE_SWITCH;
        else if (device.getType().equals(MyDeviceTypes.LIGHT) || device.getType().equals(MyDeviceTypes.ADVANCEDLIGHT))
            deviceType = DeviceTypes.TYPE_LIGHT;
        else if (device.getType().equals(MyDeviceTypes.HUMIDIFIER))
            deviceType = DeviceTypes.TYPE_HUMIDIFIER;
        else if (device.getType().equals(MyDeviceTypes.AIRBLOWER))
            deviceType = DeviceTypes.TYPE_AIR_PURIFIER;
        else if (device.getType().equals(MyDeviceTypes.RASPBERRY) || device.getType().equals(MyDeviceTypes.SERVER))
            deviceType = DeviceTypes.TYPE_DISPLAY;
        else deviceType = DeviceTypes.TYPE_GENERIC_ON_OFF;
        return deviceType;
    }


    @Override
    public Flow.Publisher createPublisherForAllAvailable() {
        Devices devices = getDevices();
        List<Control> controls = new ArrayList<>();
        for (Device device : devices.getDevices()) {
            int deviceType = createDevice(device);
            controls.add(createStatelessControl(device.getName(), deviceType, device.getUserFriendlyName()));
        }
        for (Scenario scenario : devices.getScenarios()) {
            controls.add(createStatelessControl(scenario.getName(), DeviceTypes.TYPE_REMOTE_CONTROL, scenario.getUserFriendlyName()));
        }

        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls));

    }


    private void updateToggleControl(String name, int deviceType, int status, boolean state, String userFriendlyName) {
        Context context = getBaseContext();
        Intent i = new Intent(context, SwitchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("userFriendlyName", userFriendlyName);
        i.putExtra("room", SUBTITLE);
        PendingIntent pi = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Control.StatefulBuilder control = new Control.StatefulBuilder(name, pi)
                .setTitle(userFriendlyName)
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setDeviceType(deviceType)
                .setStatus(status)
                .setControlTemplate(new ToggleTemplate(name, new ControlButton(state, "toggle")));
        if (state) {
            control.setStatusText("On");
        } else {
            control.setStatusText("Off");
        }


        updatePublisher.onNext(control.build());
    }

    private void updateHumidifier(Device device, int deviceType) {
        Context context = getBaseContext();
        Intent i = new Intent(context, HumidifierActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("userFriendlyName", device.userFriendlyName);
        i.putExtra("room", SUBTITLE);
        i.putExtra("temperature", device.temperature);
        i.putExtra("humidity", device.humidity);
        PendingIntent pi = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Control control = new Control.StatefulBuilder(device.name, pi)
                .setTitle(device.userFriendlyName)
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setStatusText("t: " + (int) device.temperature + "° h: " + device.humidity + "%")
                .setDeviceType(deviceType)
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(new ToggleTemplate(
                                device.name,
                                new ControlButton(device.state, "toggle")
                        )
                )
                .build();

        updatePublisher.onNext(control);
    }

    private void updateServer(Device device, int deviceType) {
        Context context = getBaseContext();
        Intent i = new Intent(context, WithSensorsActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ObjectMapper mapper = new ObjectMapper();
        i.putExtra("userFriendlyName", device.userFriendlyName);
        i.putExtra("room", SUBTITLE);
        i.putExtra("temperature", device.temperature);
        try {
            i.putExtra("sensors", mapper.writeValueAsString(device.sensors));
            i.putExtra("cpu", mapper.writeValueAsString(device.cpu));
            i.putExtra("memory", mapper.writeValueAsString(device.memory));
        } catch (JsonProcessingException e) {
            System.out.println("serialize error");
            e.printStackTrace();
        }

        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Control control = new Control.StatefulBuilder(device.name, pi)
                .setTitle(device.userFriendlyName)
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setStatusText("t: " + (int) device.temperature + "°")
                .setDeviceType(deviceType)
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(ControlTemplate.getNoTemplateObject())
                .build();

        updatePublisher.onNext(control);

    }

    private void updateAirFresh(AirFresh device, int deviceType) {
        Context context = getBaseContext();
        Intent i = new Intent(context, AirFreshActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("name", device.getName());
        i.putExtra("state", device.getState());
        i.putExtra("userFriendlyName", device.userFriendlyName);
        i.putExtra("alarm", device.getAlarm());
        i.putExtra("heater", device.getHeater());
        i.putExtra("temperature", device.getTemperature());
        i.putExtra("room", SUBTITLE);

        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Control.StatefulBuilder control = new Control.StatefulBuilder(device.name, pi)
                .setTitle(device.userFriendlyName)
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setDeviceType(deviceType)
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(new ToggleRangeTemplate(
                                device.name,
                                new ControlButton(device.state, "toggle"),
                                new RangeTemplate(device.name, 1, 3, device.fanLevel, 1, "On %.0f level")
                        )
                );
        if (device.state) {
            control.setStatusText("t: " + (int) device.temperature + "°");
        } else {
            control.setStatusText("t: " + (int) device.temperature + "° Off");
        }

        updatePublisher.onNext(control.build());
    }

    private void updateScenario(Scenario scenario) {
        Context context = getBaseContext();
        Intent i = new Intent(context, SwitchActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("userFriendlyName", scenario.getUserFriendlyName());
        i.putExtra("room", SUBTITLE);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);
        Control control = new Control.StatefulBuilder(scenario.getName(), pi)
                .setTitle(scenario.getUserFriendlyName())
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setStatusText("Tap to start")
                .setDeviceType(DeviceTypes.TYPE_REMOTE_CONTROL)
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(new StatelessTemplate(scenario.getName()))
                .build();

        updatePublisher.onNext(control);
    }

    private void updateToggleRangeControl(String name, int deviceType, int status, float currentValue, boolean state, String userFriendlyName, String color) {
        Context context = getBaseContext();
        Intent i = new Intent(context, LightActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ObjectMapper mapper = new ObjectMapper();
        try {
            i.putExtra("colors", "{\"palette\":" + mapper.writeValueAsString(this.colors) + "}");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        i.putExtra("brightness", currentValue);
        i.putExtra("name", name);
        i.putExtra("userFriendlyName", userFriendlyName);
        i.putExtra("room", SUBTITLE);
        i.putExtra("currentColor", color);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Control.StatefulBuilder control = new Control.StatefulBuilder(name, pi)
                .setTitle(userFriendlyName)
                .setSubtitle(SUBTITLE)
                .setStructure(STRUCTURE)
                .setDeviceType(deviceType)
                .setStatus(status)
                .setControlTemplate(new ToggleRangeTemplate(
                        name,
                        new ControlButton(state, "toggle"),
                        new RangeTemplate(name, 1.0f, 100.0f, currentValue, 1f, "On %.0f%%")));

        if (!state) {
            control.setStatusText("Off");
        }
        updatePublisher.onNext(control.build());
    }

    private ReplayProcessor updatePublisher;

    @Override
    public Flow.Publisher createPublisherFor(List<String> controlIds) {
        updatePublisher = ReplayProcessor.create();
        this.controlIds = controlIds;
        Devices devices = getDevices();
        for (Device device : devices.getDevices()) {
            this.devices.put(device.getName(), device);
        }
        for (Scenario sc : devices.getScenarios()) {
            scenarioMap.put(sc.getName(), sc);
        }
        colors = devices.getPalette();


        for (String key : controlIds) {
            Device device = this.devices.get(key);
            if (device == null) {
                updateScenario(Objects.requireNonNull(scenarioMap.get(key)));
            } else {
                int deviceType = createDevice(device);

                if (device.getType().equals(MyDeviceTypes.LIGHT) || device.getType().equals(MyDeviceTypes.ADVANCEDLIGHT)) {
                    updateToggleRangeControl(device.name, deviceType, Control.STATUS_OK, device.getBrightness(), device.getState(), device.userFriendlyName, device.color);
                } else if (device.getType().equals(MyDeviceTypes.HUMIDIFIER)) {
                    updateHumidifier(device, deviceType);
                } else if (device.getType().equals(MyDeviceTypes.AIRBLOWER)) {
                    AirFresh airFresh = new AirFresh(device);
                    updateAirFresh(airFresh, deviceType);
                } else if (device.getType().equals(MyDeviceTypes.RASPBERRY)) {
                    updateServer(device, deviceType);
                } else if (device.getType().equals(MyDeviceTypes.SERVER)) {
                    updateServer(device, deviceType);
                } else {
                    updateToggleControl(device.name, deviceType, Control.STATUS_OK, device.getState(), device.userFriendlyName);
                }
            }

        }
        return FlowAdapters.toFlowPublisher(updatePublisher);

    }


    @Override
    public void performControlAction(String controlId, ControlAction action,
                                     Consumer consumer) {
        consumer.accept(ControlAction.RESPONSE_OK);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        if (action instanceof BooleanAction) {
            BooleanAction currentAction = (BooleanAction) action;
            AtomicBoolean state = new AtomicBoolean(false);
            Device d = this.devices.get(controlId);
            assert d != null;
            Switch sw = new Switch(d);

//            sw.setState(d.getState());
//            sw.setBrightness(d.getBrightness());
//            sw.setColor(d.getColor());
//            sw.setType(d.getType());
//            sw.setUserFriendlyName(d.getUserFriendlyName());
            if (currentAction.getNewState()) {
                executor.execute(() -> {
                    try {
                        sw.turnOn();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        state.set(true);
                    }
                });
            } else {
                executor.execute(() -> {
                    try {
                        sw.turnOff();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        state.set(true);
                    }
                });

            }
            int deviceType = createDevice(sw);
            sw.state = currentAction.getNewState();
            if (sw.getType().equals(MyDeviceTypes.LIGHT) || sw.getType().equals(MyDeviceTypes.ADVANCEDLIGHT))
                updateToggleRangeControl(sw.name, deviceType, Control.STATUS_OK, sw.getBrightness(), currentAction.getNewState(), sw.userFriendlyName, sw.color);
            else if (sw.getType().equals(MyDeviceTypes.HUMIDIFIER)) {
                updateHumidifier(sw, deviceType);
            } else if (sw.getType().equals(MyDeviceTypes.AIRBLOWER)) {
                AirFresh airFresh = new AirFresh(sw);
                updateAirFresh(airFresh, deviceType);
            } else
                updateToggleControl(sw.name, deviceType, Control.STATUS_OK, currentAction.getNewState(), sw.userFriendlyName);
            this.devices.put(sw.name, sw);

        } else if (action instanceof FloatAction) {
            FloatAction currentAction = (FloatAction) action;
            AtomicBoolean state = new AtomicBoolean(false);
            Device d = this.devices.get(controlId);
            assert d != null;
            if (d.getType().equals(MyDeviceTypes.LIGHT) || d.getType().equals(MyDeviceTypes.ADVANCEDLIGHT)) {
                Light light = new Light(d);
                executor.execute(() -> {
                    try {
                        light.changeBrightness(currentAction.getNewValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        state.set(true);
                    }
                });
//                while (!state.get()) ;
                int deviceType = createDevice(light);
                updateToggleRangeControl(light.name, deviceType, Control.STATUS_OK, currentAction.getNewValue(), true, light.userFriendlyName, light.color);
                light.setBrightness(currentAction.getNewValue());
                this.devices.put(light.name, light);
            } else if (d.getType().equals(MyDeviceTypes.AIRBLOWER)) {
                AirFresh airFresh = new AirFresh(d);
                executor.execute(() -> {
                    try {
                        airFresh.changeFanLevel(currentAction.getNewValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        state.set(true);
                    }
                });
                while (!state.get()) ;
                int deviceType = createDevice(airFresh);
                airFresh.setFanLevel(currentAction.getNewValue());
                airFresh.setState(true);
                updateAirFresh(airFresh, deviceType);
                this.devices.put(airFresh.name, airFresh);
            }

        } else if (action instanceof CommandAction) {
            executor.execute(() -> {
                Scenario sc = scenarioMap.get(controlId);
                assert sc != null;
                try {
                    sc.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


    }
}

package com.example.iot2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.iot2.dataclasses.CPU;
import com.example.iot2.dataclasses.Memory;
import com.example.iot2.dataclasses.Sensor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class WithSensorsActivity extends BaseActivity {

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ObjectMapper mapper = new ObjectMapper();
        List<Sensor> sensors = null;
        CPU cpu = null;
        Memory memory = null;
        try {
            sensors = mapper.readValue(intent.getStringExtra("sensors"), new TypeReference<List<Sensor>>() {});
            cpu = mapper.readValue(intent.getStringExtra("cpu"), CPU.class);
            memory = mapper.readValue(intent.getStringExtra("memory"), Memory.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ListView list = findViewById(R.id.sensors);

        Adapter adapter = new Adapter(sensors, this);
        list.setAdapter(adapter);

        TextView cpuUsage = findViewById(R.id.value);
        TextView memoryUsage = findViewById(R.id.memory_usage);
        TextView cpuFreq = findViewById(R.id.freq);
        TextView memoryGB = findViewById(R.id.mem_gb);

        assert cpu != null;
        assert memory != null;
        cpuUsage.setText(String.format("%.1f", cpu.getPercent()) + "%");
        memoryUsage.setText(String.format("%.1f", memory.getPercent()) + "%");
        cpuFreq.setText(String.format("%d/%d", cpu.getFreq().getValue(), cpu.getFreq().getMax()));
        memoryGB.setText(String.format("%.1f/%.1f", memory.getUsed(), memory.getTotal()));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_with_sensors;
    }
}
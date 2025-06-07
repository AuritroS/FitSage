package com.example.fitsage.data.remote.dto;
public class GenerateWorkoutRequestDto {
    private int duration;
    private String focus;
    public GenerateWorkoutRequestDto(int duration, String focus) {
        this.duration = duration;
        this.focus = focus;
    }
    public int getDuration() {return duration;}
    public String getFocus() {return focus;}
}

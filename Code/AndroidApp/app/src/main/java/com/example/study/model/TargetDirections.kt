package com.example.study.model

data class TargetDirections(
    val targetDirectionsVertical: List<String> = listOf(
            "Up",
            "Up_out",
            "Down",
            "Down_out",
            "Up",
            "Up_out",
            "Down",
            "Down_out"
            ),
    val targetDirectionsHorizontal: List<String> = listOf(
            "Left",
            "Left_out",
            "Right",
            "Right_out",
            "Left",
            "Left_out",
            "Right",
            "Right_out"
    ),
    val targetDirections2D: List<String> = listOf(
            "Up_left",
            "Up_right",
            "Down_left",
            "Down_right",
            "Up_out_left",
            "Up_out_right",
            "Down_out_left",
            "Down_out_right"
    )
)

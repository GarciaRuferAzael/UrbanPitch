package com.example.urbanpitch.utils

enum class PermissionStatus {
    Unknown,
    Granted,
    Denied,
    PermanentlyDenied;
    val isGranted get() = this == Granted
    val isDenied get() =
        this == Denied ||
                this == PermanentlyDenied
}
package com.traversoft.gdgphotoshare.data;


import android.support.annotation.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface BroadcastableAction {

    enum Type {
        CAMERA_APPROVED("camera.approved"),
        CAMERA_DENIED("camera.denied"),
        WRITE_APPROVED("write.approved"),
        WRITE_DENIED("write.denied");

        @Getter private @NonNull String action;
        Type(@NonNull String action) {
            this.action = "com.traversoft.gdgphotoshare." + action;
        }
    }

    @NonNull Type getType();

    @AllArgsConstructor
    class CameraPermissionApproved implements BroadcastableAction {
        @Override public @NonNull Type getType() { return Type.CAMERA_APPROVED; }
    }

    @AllArgsConstructor
    class CameraPermissionDenied implements BroadcastableAction {
        @Override public @NonNull Type getType() { return Type.CAMERA_DENIED; }
    }

    @AllArgsConstructor
    class WritePermissionApproved implements BroadcastableAction {
        @Override public @NonNull Type getType() { return Type.WRITE_APPROVED; }
    }

    @AllArgsConstructor
    class WritePermissionDenied implements BroadcastableAction {
        @Override public @NonNull Type getType() { return Type.WRITE_DENIED; }
    }

}

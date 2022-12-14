package org.example.lock.jdbc;

import java.time.Duration;
import java.util.Objects;

public final class LockRequest {
    private final String lockId;
    private final String ownerId;
    private final Duration duration;

    public LockRequest(
            String lockId,
            String ownerId,
            Duration duration) {
        this.lockId = lockId;
        this.ownerId = ownerId;
        this.duration = duration;
    }

    public LockRequest(
            String lockId,
            String ownerId) {
        this.lockId = lockId;
        this.ownerId = ownerId;
        this.duration = null;
    }

    public String getLockId() {
        return lockId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LockRequest request = (LockRequest) o;
        return Objects.equals(lockId, request.lockId) &&
                Objects.equals(ownerId, request.ownerId) &&
                Objects.equals(duration, request.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lockId, ownerId, duration);
    }

    @Override
    public String toString() {
        return "LockRequest{" +
                "lockId=" + lockId +
                ", ownerId=" + ownerId +
                ", duration=" + duration +
                '}';
    }
}
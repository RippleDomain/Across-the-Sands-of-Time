package com.halenteck.fpsGame;

import org.joml.Vector3f;

public class Bullet {
    private static final float SPEED = 50f;

    private Vector3f position;
    private Vector3f velocity;

    private int damage;

    private Player player;
    private FPSWeapon weapon;

    public Bullet(Vector3f startPosition, Vector3f direction, int damage, Player player) {
        this.position = new Vector3f(startPosition.x, startPosition.y + 1.7f, startPosition.z);
        direction.normalize();
        this.velocity = new Vector3f(direction.x * SPEED, direction.y * SPEED, direction.z * SPEED);
        this.damage = damage;
        this.player = player;
    }

    public boolean doesBulletHitTarget(Player player) {
        for (float i = 0; i < 100; i+= 0.1f) {
            update(i);
            if (isBulletHittingTarget(this, player)) return true;
        }
        return false;
    }

    private boolean isBulletHittingTarget(Bullet bullet, Player target) {
        float hitRadius = 0.8f;

        float targetX = target.getX();
        float targetY = target.getY();
        float targetZ = target.getZ();

        float bulletX = bullet.getPosition().x;
        float bulletY = bullet.getPosition().y;
        float bulletZ = bullet.getPosition().z;

        if ((bulletX >= targetX - 0.2f && bulletX <= targetX + 0.2f) &&
                (bulletY >= targetY && bulletY <= targetY + 1.7f ) &&
                (bulletZ >= targetZ - 0.2f && bulletZ <= targetZ + 0.2f)) {
            if (this.player.isAbilityActive() && this.player.getCharacterId() == 0x03) {
                damage = 100;
                return true;
            }
            else {
                damage = weapon.getDamage();
                return true;
            }
        }
        else if (this.player.isAbilityActive() && this.player.getCharacterId() == 0x03) {
            if ((bulletX >= targetX - 0.2f - hitRadius && bulletX < targetX - 0.2f) ||
                    (bulletX <= targetX + 0.2f + hitRadius && bulletX > targetX + 0.2f) ||
                    (bulletY <= targetY + 1.7f + hitRadius && bulletY > targetY + 1.7f) ||
                    (bulletZ >= targetZ - 0.2f - hitRadius && bulletZ < targetZ - 0.2f) ||
                    (bulletZ <= targetZ + 0.2f + hitRadius && bulletZ > targetZ + 0.2f)) {
                damage = weapon.getDamage() / 30;
                return true;
            }
        }
        return false;
    }

    public void update(float time) {
        position.add(new Vector3f(velocity.mul(0.1f)));
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getDamage() {
        return damage;
    }

    public Player getPlayer() {
        return player;
    }
}

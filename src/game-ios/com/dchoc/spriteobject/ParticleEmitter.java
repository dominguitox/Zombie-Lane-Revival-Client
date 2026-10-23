package com.dchoc.spriteobject;

import com.dchoc.toolkit.MathUtils;

public class ParticleEmitter {
   private int animationIndex;
   private SpriteObject[] animations;
   private int duration;
   private boolean dynamicEmitterPosition;
   private int elapsedTime;
   private int emissionInterval;
   private boolean fixedParticleLife;
   private int gravityX;
   private int gravityY;
   private int height;
   private int id;
   private int lastEmissionTime;
   private int maxParticleCount;
   private int particleMaxAcceleration;
   private int particleMaxAngle;
   private int particleMaxEndVelocity;
   private int particleMaxLife;
   private int particleMaxStartVelocity;
   private int particleMinAcceleration;
   private int particleMinAngle;
   private int particleMinEndVelocity;
   private int particleMinLife;
   private int particleMinStartVelocity;
   private int particleSpreadStep;
   private ParticleObject[] particles;
   private int width;
   private int x;
   private int y;
   private boolean drawNewestParticleFirst = true;
   private boolean randomizeAnimationSelection = true;
   private boolean randomizeParticleSpread = true;
   private int currentAngle = -2147483647;

   public ParticleEmitter(int x, int y, int width, int height) {
      this.setBounds(x, y, width, height);
   }

   public ParticleEmitter clone() {
      ParticleEmitter copy = new ParticleEmitter(0, 0, 0, 0);
      copy.setMaxParticleCount(this.particles == null ? 0 : this.particles.length);
      copy.animations = new SpriteObject[this.animations == null ? 0 : this.animations.length];

      for (int i = 0; i < copy.animations.length; i++) {
         copy.animations[i] = this.animations[i].clone();
      }

      copy.x = this.x;
      copy.y = this.y;
      copy.width = this.width;
      copy.height = this.height;
      copy.id = this.id;
      copy.duration = this.duration;
      copy.emissionInterval = this.emissionInterval;
      copy.drawNewestParticleFirst = this.drawNewestParticleFirst;
      copy.randomizeAnimationSelection = this.randomizeAnimationSelection;
      copy.dynamicEmitterPosition = this.dynamicEmitterPosition;
      copy.fixedParticleLife = this.fixedParticleLife;
      copy.randomizeParticleSpread = this.randomizeParticleSpread;
      copy.particleSpreadStep = this.particleSpreadStep;
      copy.particleMinLife = this.particleMinLife;
      copy.particleMaxLife = this.particleMaxLife;
      copy.particleMinStartVelocity = this.particleMinStartVelocity;
      copy.particleMaxStartVelocity = this.particleMaxStartVelocity;
      copy.particleMinEndVelocity = this.particleMinEndVelocity;
      copy.particleMaxEndVelocity = this.particleMaxEndVelocity;
      copy.particleMinAcceleration = this.particleMinAcceleration;
      copy.particleMaxAcceleration = this.particleMaxAcceleration;
      copy.particleMinAngle = this.particleMinAngle;
      copy.particleMaxAngle = this.particleMaxAngle;
      copy.gravityX = this.gravityX;
      copy.gravityY = this.gravityY;
      copy.lastEmissionTime = 0;
      return copy;
   }

   public void setMaxParticleCount(int count) {
      this.maxParticleCount = count;
      this.particles = new ParticleObject[count];

      for (int i = count - 1; i >= 0; i--) {
         this.particles[i] = new ParticleObject();
      }

      this.reset();
   }

   public void setElapsedTime(int time, int x, int y) {
      if (time == 0) {
         this.reset();
      } else if (time - this.elapsedTime > 0) {
         this.update(time - this.elapsedTime, x, y);
      }

      this.elapsedTime = time;
   }

   private void reset() {
      for (int i = this.maxParticleCount - 1; i >= 0; i--) {
         this.particles[i].setAlive(false);
      }

      this.animationIndex = 0;
      this.currentAngle = -2147483647;
      this.elapsedTime = 0;
   }

   private void update(int delta, int offsetX, int offsetY) {
      this.elapsedTime += delta;
      int available = this.maxParticleCount;

      for (int i = this.maxParticleCount - 1; i >= 0; i--) {
         if (this.particles[i].isAlive()) {
            this.particles[i].update(delta);
            available--;
         }
      }

      if (this.isActive()) {
         if (this.emissionInterval == 0) {
            for (int i = 0; i < available; i++) {
               this.addParticle(offsetX, offsetY);
            }
         } else {
            if (this.elapsedTime < this.lastEmissionTime) {
               this.lastEmissionTime = 0;
            }

            int deltaEmission = this.elapsedTime - this.lastEmissionTime;
            if (deltaEmission >= this.emissionInterval) {
               int count = Math.min(deltaEmission / this.emissionInterval, available);

               for (int i = 0; i < count; i++) {
                  this.addParticle(offsetX, offsetY);
               }

               this.lastEmissionTime = this.elapsedTime;
            }
         }
      }
   }

   private void addParticle(int offsetX, int offsetY) {
      ParticleObject particle = null;
      if (this.drawNewestParticleFirst) {
         for (int i = 0; i < this.maxParticleCount; i++) {
            if (!this.particles[i].isAlive()) {
               particle = this.particles[i];
               break;
            }
         }
      } else {
         for (int i = this.maxParticleCount - 1; i >= 0; i--) {
            if (!this.particles[i].isAlive()) {
               particle = this.particles[i];
               break;
            }
         }
      }

      if (particle != null) {
         int index;
         if (this.randomizeAnimationSelection) {
            index = MathUtils.random(this.animations == null ? 0 : this.animations.length);
         } else {
            index = this.animationIndex;
            this.animationIndex = (this.animationIndex + 1) % (this.animations == null ? 0 : this.animations.length);
         }

         particle.setAnimation(this.animations[index]);
         particle.setStartLocation(this.x + offsetX + MathUtils.random(this.width), this.y + offsetY + MathUtils.random(this.height));
         particle.setDuration(
            this.fixedParticleLife
               ? this.animations[index].getAnimationLength()
               : this.particleMinLife + MathUtils.random(this.particleMaxLife - this.particleMinLife)
         );
         particle.setGravity(this.gravityX, this.gravityY);
         particle.setVelocity(this.particleMinEndVelocity, this.particleMaxEndVelocity);
         particle.setVelocity(
            this.particleMaxStartVelocity == this.particleMinStartVelocity
               ? this.particleMaxStartVelocity
               : this.particleMinStartVelocity + MathUtils.random(this.particleMaxStartVelocity - this.particleMinStartVelocity)
         );
         particle.setAcceleration(
            this.particleMaxAcceleration == this.particleMinAcceleration
               ? this.particleMaxAcceleration
               : this.particleMinAcceleration + MathUtils.random(this.particleMaxAcceleration - this.particleMinAcceleration)
         );
         int angle = this.particleMinAngle;
         if (this.particleMaxAngle != this.particleMinAngle) {
            if (this.isRandomizeParticleSpread()) {
               angle = this.particleMinAngle + MathUtils.random(this.particleMaxAngle - this.particleMinAngle);
            } else {
               if (this.currentAngle == -2147483647) {
                  this.currentAngle = this.particleMinAngle;
               }

               angle = this.currentAngle;
               this.currentAngle = this.currentAngle + this.particleSpreadStep;
               if (this.currentAngle < this.particleMinAngle) {
                  this.currentAngle = this.particleMaxAngle;
               } else if (this.currentAngle > this.particleMaxAngle) {
                  this.currentAngle = this.particleMinAngle;
               }
            }
         }

         particle.setAngle(angle);
         particle.setAlive(true);
      }
   }

   public SpriteObject[] getAnimationList() {
      return this.animations;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getEmissionInterval() {
      return this.emissionInterval;
   }

   public int getGravityX() {
      return this.gravityX;
   }

   public int getGravityY() {
      return this.gravityY;
   }

   public int getHeight() {
      return this.height;
   }

   public int getId() {
      return this.id;
   }

   public int getMaxParticleCount() {
      return this.maxParticleCount;
   }

   public int getParticleMaxAcceleration() {
      return this.particleMaxAcceleration;
   }

   public int getParticleMaxAngle() {
      return this.particleMaxAngle;
   }

   public int getParticleMaxEndVelocity() {
      return this.particleMaxEndVelocity;
   }

   public int getParticleMaxLife() {
      return this.particleMaxLife;
   }

   public int getParticleMaxStartVelocity() {
      return this.particleMaxStartVelocity;
   }

   public int getParticleMinAcceleration() {
      return this.particleMinAcceleration;
   }

   public int getParticleMinAngle() {
      return this.particleMinAngle;
   }

   public int getParticleMinEndVelocity() {
      return this.particleMinEndVelocity;
   }

   public int getParticleMinLife() {
      return this.particleMinLife;
   }

   public int getParticleMinStartVelocity() {
      return this.particleMinStartVelocity;
   }

   public int getParticleSpreadStep() {
      return this.particleSpreadStep;
   }

   public int getWidth() {
      return this.width;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public boolean hasLiveParticles() {
      int i = this.maxParticleCount;

      while (--i >= 0) {
         if (this.particles[i].isAlive()) {
            return true;
         }
      }

      return false;
   }

   public boolean isActive() {
      return this.duration == 0 || this.elapsedTime < this.duration;
   }

   public boolean isDrawNewestParticleFirst() {
      return this.drawNewestParticleFirst;
   }

   public boolean isDynamicEmitterPosition() {
      return this.dynamicEmitterPosition;
   }

   public boolean isFixedParticleLife() {
      return this.fixedParticleLife;
   }

   public boolean isRandomizeAnimationSelection() {
      return this.randomizeAnimationSelection;
   }

   public boolean isRandomizeParticleSpread() {
      return this.randomizeParticleSpread;
   }

   public void render(int i, int i2) {
      for (int i3 = 0; i3 < this.maxParticleCount; i3++) {
         this.particles[i3].render(i, i2);
      }
   }

   public void setAnimations(SpriteObject[] spriteObjectArr) {
      this.animations = spriteObjectArr;
   }

   public void setBounds(int i, int i2, int i3, int i4) {
      this.x = i;
      this.y = i2;
      this.width = i3;
      this.height = i4;
   }

   public void setDrawNewestParticleFirst(boolean z) {
      this.drawNewestParticleFirst = z;
   }

   public void setDuration(int i) {
      this.duration = i;
   }

   public void setDynamicEmitterPosition(boolean z) {
      this.dynamicEmitterPosition = z;
   }

   public void setEmissionInterval(int i) {
      this.emissionInterval = i;
   }

   public void setFixedParticleLife(boolean z) {
      this.fixedParticleLife = z;
   }

   public void setGravity(int i, int i2) {
      this.gravityX = i;
      this.gravityY = i2;
   }

   public void setGravityX(int i) {
      this.gravityX = i;
   }

   public void setGravityY(int i) {
      this.gravityY = i;
   }

   public void setHeight(int i) {
      this.height = i;
   }

   public void setId(int i) {
      this.id = i;
   }

   public void setParticleMaxAcceleration(int i) {
      this.particleMaxAcceleration = i;
   }

   public void setParticleMaxAngle(int i) {
      this.particleMaxAngle = i;
   }

   public void setParticleMaxEndVelocity(int i) {
      this.particleMaxEndVelocity = i;
   }

   public void setParticleMaxLife(int i) {
      this.particleMaxLife = i;
   }

   public void setParticleMaxStartVelocity(int i) {
      this.particleMaxStartVelocity = i;
   }

   public void setParticleMinAcceleration(int i) {
      this.particleMinAcceleration = i;
   }

   public void setParticleMinAngle(int i) {
      this.particleMinAngle = i;
   }

   public void setParticleMinEndVelocity(int i) {
      this.particleMinEndVelocity = i;
   }

   public void setParticleMinLife(int i) {
      this.particleMinLife = i;
   }

   public void setParticleMinStartVelocity(int i) {
      this.particleMinStartVelocity = i;
   }

   public void setParticleSpreadStep(int i) {
      this.particleSpreadStep = i;
   }

   public void setRandomizeAnimationSelection(boolean z) {
      this.randomizeAnimationSelection = z;
   }

   public void setRandomizeParticleSpread(boolean z) {
      this.randomizeParticleSpread = z;
   }

   public void setWidth(int i) {
      this.width = i;
   }

   public int setX(int i) {
      this.x = i;
      return i;
   }

   public int setY(int i) {
      this.y = i;
      return i;
   }

   public void translate(int i, int i2) {
      this.x += i;
      this.y += i2;
   }
}

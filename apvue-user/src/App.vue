<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import * as THREE from 'three'
import { useRoute } from 'vue-router'
import { NConfigProvider, NGlobalStyle, NMessageProvider, NDialogProvider } from 'naive-ui'

// --- Vanta Background Logic ---
const vantaRef = ref<HTMLElement | null>(null)
let vantaEffect: any = null
const route = useRoute()
const isAuthPage = ref(false)

// Initialize Vanta
const initVanta = () => {
  if (vantaEffect) return
  if (!vantaRef.value) {
      console.warn('Vanta ref not found')
      return
  }

  try {
    // Ensure THREE is globally available for Vanta
    if (typeof window !== 'undefined') {
        (window as any).THREE = THREE;
    }

    vantaEffect = (window as any).VANTA.BIRDS({
        el: vantaRef.value,
        mouseControls: true,
        touchControls: true,
        gyroControls: false,
        minHeight: 200.00,
        minWidth: 200.00,
        scale: 1.00,
        scaleMobile: 1.00,
        // Fall Theme (Blue Variant)
        backgroundColor: 0x000000, 
        color1: 0x87ceeb, // Sky Blue
        color2: 0x00bfff, // Deep Sky Blue
        colorMode: "lerp",
        birdSize: 2.50,
        wingSpan: 10.00,
        speedLimit: 1.50,
        separation: 100.00,
        alignment: 50.00,
        cohesion: 5.00,
        quantity: 3.00, // Increased quantity
        backgroundAlpha: 0.0
    })
    console.log('Vanta initialized successfully via CDN')
  } catch (error) {
    console.error("Vanta init error:", error)
  }
}

const destroyVanta = () => {
  if (vantaEffect) {
    try {
        vantaEffect.destroy()
    } catch (e) {
        console.warn('Vanta destroy error', e)
    }
    vantaEffect = null
  }
}

// Handle Route Changes
watch(() => route.name, async (newRouteName) => {
  const name = newRouteName?.toString().toLowerCase() || ''
  isAuthPage.value = ['login', 'register'].includes(name)
  
  await nextTick() // Wait for DOM

  if (isAuthPage.value) {
    destroyVanta()
  } else {
    if (!vantaEffect) {
        initVanta()
    }
  }
}, { immediate: true })

// Init on mount if not auth page
onMounted(async () => {
    await nextTick()
    const name = route.name?.toString().toLowerCase() || ''
    isAuthPage.value = ['login', 'register'].includes(name)
    if (!isAuthPage.value) {
        initVanta()
    }
})
</script>

<template>
  <n-config-provider>
    <n-global-style />
    <n-message-provider>
      <n-dialog-provider>
        <!-- Static Background Image -->
        <div class="static-bg" :class="{ hidden: isAuthPage }"></div>
        
        <!-- Vanta Background Container -->
        <div ref="vantaRef" class="vanta-bg" :class="{ hidden: isAuthPage }"></div>
        
        <div class="app-content">
            <router-view />
        </div>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<style>
html, body, #app {
  height: 100%;
  margin: 0;
}

/* Static Background Image */
.static-bg {
    position: fixed;
    z-index: -2; /* Behind Vanta */
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('/src/assets/bizhi.jpg');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    opacity: 0.45;
    pointer-events: none;
    transition: opacity 0.5s ease;
}

.static-bg.hidden {
    opacity: 0;
    pointer-events: none;
}

/* Vanta Background Styles */
.vanta-bg {
  position: fixed;
  z-index: -1; /* Behind content, In front of static bg */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; 
}

.vanta-bg.hidden {
    display: none;
}

.app-content {
  position: relative;
  z-index: 1;
  min-height: 100%;
}
</style>
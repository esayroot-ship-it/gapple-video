declare module 'vue-cropper' {
    import { DefineComponent } from 'vue'
    const VueCropper: DefineComponent<{}, {}, any>
    export { VueCropper }
    const component: DefineComponent<{}, {}, any>
    export default component
}

declare module '*/vue-cropper.vue' {
    import { DefineComponent } from 'vue'
    const component: DefineComponent<{}, {}, any>
    export default component
}

const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    lintOnSave: false,
    configureWebpack: {
        resolve: {
            fallback: {
                fs: false,
                zlib: require.resolve("browserify-zlib")
            }
        }
    }
})

module.exports = {
    resolve: {
        extensions: [".js", ".ts", ".tsx"]
    },
    output: {
        filename: 'js/[name].[contenthash].js',
        chunkFilename: 'chunks/[name].[contenthash].js',
        publicPath: './'
    },
    optimization: {
        splitChunks: {
            chunks: "all",
            minSize: 30000,
            automaticNameDelimiter: '~',
        },
    },
}
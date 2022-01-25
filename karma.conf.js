module.exports = function (config) {
    config.set({
        browsers: ['jsdom'],
        // The directory where the output file lives
        basePath: 'target',
        // The file itself
        files: ['karma.js'],
        frameworks: ['cljs-test'],
        plugins: ['karma-cljs-test', 'karma-jsdom-launcher'],
        colors: true,
        logLevel: config.LOG_INFO,
        client: {
            args: ["shadow.test.karma.init"],
            singleRun: true
        }
    })
};

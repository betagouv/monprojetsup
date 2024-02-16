install webpack
install npm

to test: 'npm start'
to build: 'npm run build'
to deploy on LaBRI website: `npm run deploy`

config VSCODE pour débugguer dans Chrome, fichier .vscode/launch.json

{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
          "trace": true,
          "name": "Debug in VSCode via NPM+webpack",
          "type": "chrome",
          "request": "launch",
          "url": "http://localhost:8085/",
          "webRoot": "${workspaceRoot}/",
          "userDataDir": "${workspaceRoot}/.vscode/chrome",
          "sourceMaps": true,
          "disableNetworkCache": true,
          "runtimeArgs": ["--disable-web-security"]
        }
    ]
}


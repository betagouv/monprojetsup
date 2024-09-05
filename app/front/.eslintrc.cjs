module.exports = {
  root: true,
  env: { browser: true, es2020: true },
  extends: [
    'eslint:recommended',
    'plugin:react-hooks/recommended',
    "plugin:sonarjs/recommended-legacy",
    "plugin:import/recommended",
    "plugin:import/typescript",
    "canonical/auto",
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs', '*.gen.ts', 'api-mps.d.ts'],
  parser: '@typescript-eslint/parser',
  plugins: [
    'react-refresh',
    "sonarjs",
    "react-hooks",
    "import",
    "no-relative-import-paths",
    "testing-library"
  ],
  rules: {
    "sonarjs/class-name": ["error", { format: "^[A-Za-zÀ-ÖØ-öø-ÿ]*$" }],
    "sonarjs/slow-regex": "off",
    "typescript-sort-keys/interface": "off",
    "react/no-danger": "off",
    "@typescript-eslint/consistent-type-definitions": "off",
    "@typescript-eslint/no-unused-vars": [
      "error",
      {
        "args": "all",
        "argsIgnorePattern": "^_",
        "caughtErrors": "all",
        "caughtErrorsIgnorePattern": "^_",
        "destructuredArrayIgnorePattern": "^_",
        "varsIgnorePattern": "^_",
        "ignoreRestSiblings": true
      }
    ],
    "no-relative-import-paths/no-relative-import-paths": [
      "error",
      {
        "allowSameFolder": true,
        "rootDir": "src",
        "prefix": "@"
      }
    ],
    "prettier/prettier": [
      "error",
      {
        "printWidth": 120
      }
    ],
    "unicorn/prevent-abbreviations": "off",
    "canonical/sort-keys": "off",
    "canonical/sort-react-dependencies": "off",
    "canonical/filename-match-regex": "off",
    "canonical/filename-match-exported": "off",
    "canonical/id-match": "off",
    "import/no-unassigned-import": [
      2,
      {
        "allow": [
          "**/*.scss"
        ]
      }
    ],
    "react/jsx-max-props-per-line": [
      2,
      {
        "maximum": 1
      }
    ],
    'react-refresh/only-export-components': [
      'warn',
      { allowConstantExport: true },
    ],
    "react/forbid-component-props": [
      2,
      {
        "forbid": [
          {
            "propName": "className",
            "allowedFor": [
              "Link",
              "Accordion.Root",
              "Accordion.Item",
              "Accordion.Header",
              "Accordion.Trigger",
              "Accordion.Content",
            ]
          }
        ]
      }
    ]
  },
}

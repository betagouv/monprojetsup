import browser from "eslint-config-canonical/configurations/browser.js";
import canonical from "eslint-config-canonical/configurations/canonical.js";
import json from "eslint-config-canonical/configurations/json.js";
import react from "eslint-config-canonical/configurations/react.js";
import regexp from "eslint-config-canonical/configurations/regexp.js";
import vitest from "eslint-config-canonical/configurations/vitest.js";
import yaml from "eslint-config-canonical/configurations/yaml.js";
import zod from "eslint-config-canonical/configurations/zod.js";
import sonarjs from "eslint-plugin-sonarjs";
import prettierPlugin from "eslint-plugin-prettier/recommended";
import globals from "globals";
import tseslint from "typescript-eslint";
import eslintConfigPrettier from "eslint-config-prettier";
import noRelativeImportPaths from 'eslint-plugin-no-relative-import-paths';

export default [
  {
    ignores: [
      "**/dist/*",
      ".eslintrc.cjs",
      "**/*.gen.ts",
      "**/api-mps.d.ts",
      "**/i18next-generated.d.ts",
      "**/public/dsfr/*",
      "**/playwright.config.ts",
      "tsconfig.json",
      "package.json",
      "eslint.config.js"
    ],
  },
  {
    languageOptions: {
      ecmaVersion: 2021,
      parserOptions: {
        projectService: true,
        tsconfigRootDir: import.meta.dirname,
      },
      globals: {
        ...globals.browser,
        ...globals.builtin,
        ...vitest.recommended.plugins.vitest.environments.env.globals,
      }
    },
  },
  {
    ...canonical.recommended,
    rules: {
      ...canonical.recommended.rules,
      "quotes": [
        2,
        "double",
      ],
      "import/extensions": "off",
      "no-useless-constructor": "off",
      "array-bracket-newline": [
        "error",
        { multiline: true },
      ],
      "@stylistic/quotes": "off",
      "@stylistic/object-property-newline": "off",
      "@stylistic/object-curly-newline": "off",
      "@stylistic/array-bracket-newline": "off",
      "@stylistic/array-element-newline": "off",
      "@stylistic/space-before-function-paren": "off",
      "@stylistic/operator-linebreak": "off",
      "@stylistic/nonblock-statement-body-position": "off",
      "@stylistic/jsx-quotes": "off",
      "@stylistic/no-extra-parens": "off",
      "@stylistic/implicit-arrow-linebreak": "off",
      "@stylistic/indent": "off",
      "@stylistic/no-confusing-arrow": "off",
      "arrow-body-style": "off",
      "object-property-newline": ["error", { "allowAllPropertiesOnSameLine": true }],
      "perfectionist/sort-interfaces": "off",
      "perfectionist/sort-classes": "off",
      "perfectionist/sort-objects": "off",
      "perfectionist/sort-object-types": "off",
      "perfectionist/sort-union-types": "off",
      "perfectionist/sort-switch-case": "off",
      "perfectionist/sort-intersection-types": "off",
      "perfectionist/sort-modules": "off",
      "canonical/destructuring-property-newline": "off",
      "canonical/import-specifier-newline": "off",
      "canonical/filename-match-regex": "off",
      "canonical/id-match": [
        2,
        "(^[_A-Za-zÀ-ÖØ-öø-ÿ]+(?:[A-ZÀ-Ö0-9][a-zØ-öø-ÿ0-9]*)*\\d*$)|(^[A-ZÀ-Ö]+(_[A-ZÀ-Ö]+)*(_\\d$)*$)|(^(_|\\$)$)",
        {
          ignoreDestructuring: true,
          ignoreNamedImports: true,
          onlyDeclarations: true,
          properties: true,
        },
      ],
    },
  },
  {
    ...react.recommended,
    rules: {
      ...react.recommended.rules,
      "react-hooks/rules-of-hooks": "off",
      "react/forbid-component-props": [
        2,
        {
          forbid: [
            {
              propName: "className",
              allowedFor: [
                "Balise",
                "TextareaAutosize",
                "Link",
                "Toggle",
                "Accordion.Root",
                "Accordion.Item",
                "Accordion.Header",
                "Accordion.Trigger",
                "Accordion.Content",
                "ToastRadix.Viewport",
                "ToastRadix.Root",
              ],
            },
          ],
        },
      ],
    },
  },
  {
    ...json.recommended,
    rules: {
      ...json.recommended.rules,
      "jsonc/sort-keys": "off",
    },
  },

  {
    ...vitest.recommended,
    rules: {
      ...vitest.recommended.rules,
      "vitest/no-skipped-tests": "off",
    },
  },
  {
    ...zod.recommended,
    rules: {
      ...zod.recommended.rules,
      "zod/require-strict": "off"
    }
  },
  {
    ...sonarjs.configs.recommended,
    rules: {
      ...sonarjs.configs.recommended.rules,
      "sonarjs/pluginRules-of-hooks": "off",
      "sonarjs/no-misused-promises": "off",
      "sonarjs/slow-regex": "off",
      "sonarjs/function-return-type": "off",
      "sonarjs/class-name": [
        "error",
        { format: "^[A-Za-zÀ-ÖØ-öø-ÿ]*$" },
      ],
    },
  },
  {
    plugins: {
      'no-relative-import-paths': noRelativeImportPaths,
    },
    rules: {
      "no-relative-import-paths/no-relative-import-paths": [
        "error",
        {
          "allowSameFolder": true,
          "rootDir": "src",
          "prefix": "@"
        }
      ],
    }
  },
  regexp.recommended,
  yaml.recommended,
  browser.recommended,
  ...tseslint.configs.recommendedTypeChecked.map(config => ({
    ...config,
    rules: {
      ...config.rules,
      "@typescript-eslint/only-throw-error": "off",
      "@typescript-eslint/no-misused-promises": [2, {
        "checksVoidReturn": false
      }],
    }
  })),
  eslintConfigPrettier,
  prettierPlugin
];

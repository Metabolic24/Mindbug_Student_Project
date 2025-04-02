module.exports = {
    transform: {
      '^.+\\.vue$': 'vue-jest',
      '^.+\\.js$': 'babel-jest'
    },
    moduleFileExtensions: ['js', 'json', 'vue'],
    moduleNameMapper: {
      '^@/(.*)$': '<rootDir>/src/$1'
    },
    collectCoverage: true,
    collectCoverageFrom: [
      'src/**/*.{js,vue}',
      '!src/main.js',
      '!src/router/**'
    ],
    coverageThreshold: {
      global: {
        branches: 70,
        functions: 80,
        lines: 80,
        statements: 80
      }
    }
  };
  
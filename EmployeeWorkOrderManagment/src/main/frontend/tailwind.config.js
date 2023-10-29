/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["../resources/templates/**/*.html", "../resources/js/*.js"],
    theme: {
        extend: {
            screens: {
                'windscreen': {'raw': '(min-aspect-ratio: 3/2'},
                'tailscreen': {'raw': '(min-aspect-ratio: 13/20'},
            },
            keyframes: {
                'open-menu': {
                    '0%': { transform: 'scaleY(0)'},
                    '80%': { transform: 'scaleY(1.2)'},
                    '100%': { transform: 'scaleY(1)'}
                },
            },
            animation: {
                'open-menu': 'open-menu 0.5 ease-in-out forwards',
            }
        },
    },
    plugins: [],
}
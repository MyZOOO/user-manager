// src/utils/auth.js

export function isTokenExpired(token) {
    if (!token) return true

    try {
        const payloadBase64 = token.split('.')[1]
        const payload = JSON.parse(atob(payloadBase64))

        // exp 是秒，Date.now() 是毫秒
        return payload.exp * 1000 < Date.now()
    } catch (e) {
        // token 格式异常，一律当作过期
        return true
    }
}

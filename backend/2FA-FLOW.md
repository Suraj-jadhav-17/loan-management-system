# Simple 2FA flow

1. User enters email and password at `POST /auth/v1/login`.
2. Backend validates password and email verification, then returns a short-lived verification token only (no access/refresh JWT yet).
3. First login (`MFA_SETUP_REQUIRED`): frontend calls `POST /auth/v1/authenticator/setup` with `X-Verification-Token`. Backend creates/reuses a TOTP secret and returns a QR image (`qrCodeDataUrl`) plus the `otpauth` setup URI.
4. User scans QR with Google Authenticator/Microsoft Authenticator and enters the 6-digit code at `POST /auth/v1/authenticator/verify-setup`.
5. Backend enables MFA and only then returns access and refresh tokens.
6. Later login (`MFA_REQUIRED`): user enters the authenticator code at `POST /auth/v1/authenticator/verify-login`; tokens are returned only after valid OTP.
7. Lost phone: `POST /auth/v1/recovery/request` emails a 6-digit recovery OTP. `POST /auth/v1/recovery/verify` checks it (5-minute expiry), clears the old authenticator, and returns a setup verification token. User scans a new QR and verifies a new authenticator before receiving tokens.

The existing registration, email verification, customer/user records, role handling, and loan APIs are retained.

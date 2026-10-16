export interface AuthFlowResponse {
  status: string;
  message: string;
  email?: string;
  verificationToken?: string;
  setupUri?: string;
  qrCodeDataUrl?: string;
  accessToken?: string;
  refreshToken?: string;
}

export interface LoginTokens {
  accessToken: string;
  refreshToken: string;
}

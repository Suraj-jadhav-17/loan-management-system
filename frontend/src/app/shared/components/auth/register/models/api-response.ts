export interface ApiResponse<T> {
status: number | string;
success: boolean;
message: string;
data: T;
timestamp?: string;
path?: string;
metadata?: Record<string, unknown>;
}
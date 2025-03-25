import { create } from "zustand";
import { api } from "../lib/api";
import { router } from "expo-router";
import * as SecureStore from "expo-secure-store";

interface UserInfo {
  kakaoId: number;
  userEmail: string;
  userName?: string;
  userBirth?: string;
  userGender?: string;
  userPhone?: string;
  role?: string;
}

interface AuthStore {
  user: UserInfo | null;
  token: string | null;
  setUser: (user: UserInfo) => void;
  kakaoLogin: (code: string) => Promise<void>;
  signUp: (userInfo: Omit<UserInfo, "kakaoId" | "userEmail">) => Promise<void>;
  logout: () => void;
  getUserInfo: () => Promise<UserInfo>;
}

async function fetchAccessTokenFromKakao(code: string): Promise<string> {
  console.log("🚀 백엔드에 인가 코드 전송:", code);

  const res = await api.post(`/auth/login/kakao/token?code=${code}`);
  const { data } = res.data;
  const accessToken = data.access_token;

  if (typeof accessToken !== "string") {
    throw new Error("access_token이 문자열이 아닙니다!");
  }

  console.log("🔐 access_token 조회 완료:", accessToken);

  return accessToken;
}

async function fetchUserInfoFromKakao(token: string): Promise<UserInfo> {
  const res = await api.post(`/auth/login/kakao/user?access_token=${token}`);
  const { data } = res.data;

  const user: UserInfo = {
    kakaoId: data.id,
    userEmail: data.kakao_account.email,
  };

  console.log("✅ 사용자 정보 수신:", user);
  return user;
}

async function fetchJWTFromServer(kakaoId: number): Promise<string> {
  console.log("🚀 백엔드에 kakaoId 전송:", kakaoId, typeof kakaoId);

  const res = await api.post(`/auth/login/kakao/jwt`, {
    kakao_id: kakaoId,
  });
  const { token } = res.data;

  await SecureStore.setItemAsync("userToken", token);
  console.log("🔐 jwt 저장 완료:", token);

  return token;
}

async function fetchUserInfoFromServer(jwt: string): Promise<UserInfo> {
  const res = await api.post("/auth/check");

  const { data } = res.data;

  const user: UserInfo = {
    kakaoId: data.kakao_id,
    userEmail: data.user_email,
    userName: data.user_name,
    userBirth: data.user_birth,
    userGender: data.user_gender,
    userPhone: data.user_phone,
    role: data.role,
  };

  console.log("✅ 사용자 정보 수신:", user);
  return user;
}

export const useAuthStore = create<AuthStore>((set, get) => ({
  user: null,
  token: null,

  setUser: (user) => {
    console.log("🧠 사용자 상태 설정:", user);
    set({ user });
  },

  getUserInfo: async () => {
    const token = await SecureStore.getItemAsync("userToken");
    if (!token) {
      throw new Error("로그인이 필요합니다.");
    }
    const userInfo = await fetchUserInfoFromServer(token);
    set({ user: userInfo, token });
    return userInfo;
  },

  kakaoLogin: async (code) => {
    console.log("🚀 백엔드에 카카오 인가 코드 전송:", code);

    try {
      const token = await fetchAccessTokenFromKakao(code);
      const user = await fetchUserInfoFromKakao(token);

      set({ user });

      router.replace({
        pathname: "/(auth)/SignUp",
        params: {
          kakaoId: user.kakaoId,
          userEmail: user.userEmail,
        },
      });

      console.log("➡️ 회원가입 페이지로 이동:", user);
    } catch (err) {
      console.error("❌ 카카오 로그인 실패:", err);
      throw err;
    }
  },

  signUp: async (userInfo) => {
    const { user } = get();

    console.log("🧠 사용자:", user);

    if (!user) {
      throw new Error("카카오 로그인 되지 않은 상태입니다.");
    }

    const payload = {
      ...userInfo,
      kakaoId: user.kakaoId,
      userEmail: user.userEmail,
    };

    try {
      const res = await api.post("/auth/signup", payload);
      console.log("🎉 회원가입 성공:", res.data);
      const token = await fetchJWTFromServer(user.kakaoId);
      console.log("🔐 JWT 토큰:", token);

      set({ user, token });

      router.replace("/(app)/index" as any);
    } catch (err) {
      console.error("❌ 회원가입 실패:", err);
      throw err;
    }
  },

  logout: async () => {
    console.log("👋 로그아웃 실행");
    await SecureStore.deleteItemAsync("userToken");
    set({ user: null, token: null });
  },
}));

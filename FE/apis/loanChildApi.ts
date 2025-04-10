import { api } from "@/lib/api";

export type CreateLoanRequest = {
  loan_amount: number;
  due_date: string;
  child_signature: string;
};

export type CreateLoanResponse = {
  status: string;
  message: string;
  data: {
    loanAmount: string;
    dueDate: string;
    requestDate: string;
  };
};

export type ReqItem = {
  loan_id: number;
  loan_amount: number;
  request_date: string;
  due_date: string;
};

export type LoanItem = {
  loan_id: number;
  loan_amount: number;
  last_amount: number;
  due_date: string;
  parent_name: string;
  child_credit_score: number;
};

export type CancelLoanRequest = {
  loan_id: number;
};

export type RepaymentRequest = {
  loan_id: number;
  repayment_amount: number;
  password: string;
};

export type RepaymentResponse = {
  loan_id: number;
  due_date: string;
  repayment_amount: number;
  loan_amount: number;
  last_amount: number;
  loan_status: string; // APPROVED, REPAID(상환 완료)
  child_credit_score: number;
};

export type LoanValidationResponse = {
  is_loan_allowed: boolean;
  credit_score: number;
};

export type LoanHistoryResponse = {
  loan_completed_list: {
    loan_id: number;
    loan_amount: number;
    repaid_at: string;
  }[];
};

export type RepaymentHistoryItem = {
  loan_id: number;
  repayment_date: string;
  repaid_amount: number;
  remaining_amount: number;
};

export type LoanListResponse = {
  loan_repayment_history: RepaymentHistoryItem[];
  active_loans: LoanItem[];
};

export const createLoan = async (
  payload: CreateLoanRequest
): Promise<CreateLoanResponse> => {
  try {
    const res = await api.post("/loan", payload);
    return res.data;
  } catch (error: any) {
    const message =
      error.response?.data?.message ?? "❌ 대출 요청 중 알 수 없는 오류입니다.";
    throw new Error(message); // 페이지에서 이걸 잡음
  }
};

export const getReqList = async (): Promise<ReqItem[]> => {
  try {
    const res = await api.get("/loan/child/requested");
    return res.data.data.loan_pending_list;
  } catch (error: any) {
    if (error.response?.status === 404) {
      return [];
    }
    const message =
      error.response?.data?.message ??
      "❌ 대출 요청 목록 조회 중 알 수 없는 오류입니다.";
    throw new Error(message);
  }
};

export const cancelLoan = async (payload: CancelLoanRequest): Promise<void> => {
  try {
    await api.delete(`/loan/child/requested/${payload.loan_id}`);
    console.log("⭕ 대출 취소 성공");
  } catch (error: any) {
    const message =
      error.response?.data?.message ?? "❌ 대출 취소 중 알 수 없는 오류입니다.";
    throw new Error(message);
  }
};

export const getLoanList = async (): Promise<{
  data: LoanListResponse;
  message: string;
  status: string;
}> => {
  try {
    const res = await api.get("/loan/child/approved");
    // 응답 구조 확인
    if (res.data && res.data.data) {
      res.data.data.active_loans.forEach((loan: any, index: number) => {});
    }

    return res.data;
  } catch (error: any) {
    if (error.response?.status === 404) {
      console.warn("❗️대출 목록이 비어 있습니다 (404). 빈 데이터 반환");
    }
    const message =
      error.response?.data?.message ??
      "❌ 아이 대출 목록 조회 중 알 수 없는 오류입니다.";
    throw new Error(message);
  }
};

export const repayLoan = async (
  payload: RepaymentRequest
): Promise<RepaymentResponse> => {
  try {
    console.log("대출 상환 요청: ", payload);
    const res = await api.post("/loan/repay", payload);
    console.log("🔑 대출 상환 결과:", res.data);
    return res.data.data;
  } catch (error: any) {
    if (error.response?.status === 400) {
      console.log("❌ 대출 상환 잔액 부족");
      throw new Error("잔액이 부족합니다.");
    } else if (error.response?.status === 401) {
      console.log("❌ 비밀번호 불일치");
      throw new Error("비밀번호가 일치하지 않습니다.");
    } else {
      const message =
        error.response?.data?.message ??
        "❌ 대출 상환 중 알 수 없는 오류입니다.";
      throw new Error(message);
    }
  }
};

export const getLoanValidation = async (): Promise<LoanValidationResponse> => {
  try {
    const res = await api.get("/loan/child/credit-score");
    return res.data.data;
  } catch (error: any) {
    const message =
      error.response?.data?.message ??
      "❌ 대출 상환 검증 중 알 수 없는 오류입니다.";
    throw new Error(message);
  }
};

export const getLoanHistory = async (): Promise<LoanHistoryResponse> => {
  try {
    const res = await api.get("/loan/child/repaid");
    return res.data.data.loan_completed_list;
  } catch (error: any) {
    const message =
      error.response?.data?.message ??
      "❌ 대출 내역 조회 중 알 수 없는 오류입니다.";
    throw new Error(message);
  }
};

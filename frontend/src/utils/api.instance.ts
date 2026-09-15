import { state } from "../state";
import { getAnonymousId } from "../utils/utils";

const createRequestOptions = (props: RequestOption) => {
  const {
    method = "GET",
    body,
    addId = true,
    cache = false,
    cacheDuration = 0,
  } = props;

  return {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(addId && { "X-User-ID": getAnonymousId() }),
    },
    ...(body && { body: JSON.stringify(body) }),
    ...(cache && { "Cache-Control": `max-age=${cacheDuration}` }),
  };
};

export function loadRates() {
  fetch("/api/rates")
    .then((r) => r.json())
    .then((data) => {
      state.rates = data;
      state.lastUpdated = new Date().toLocaleTimeString();
    });
}

export async function getAllAlerts() {
  try {
    const response = await fetch("/api/alerts/all", createRequestOptions({}));

    if (response.ok) {
      const data = await response.json();
      state.alerts = data;
      return;
    }
    state.errors = { ...state.errors, alerts: true };
  } catch (error) {
    console.error(`There was an error getting all alerts. Error: ${error}`);
    state.errors = { ...state.errors, alerts: true };
  }
}

export async function getCurrencies() {
  try {
    const response = await fetch(
      "/api/currencies",
      createRequestOptions({ addId: false, cache: true, cacheDuration: 86400 }),
    );

    if (response.ok) {
      const data = await response.json();
      state.currencies = data;
      return;
    }
    state.errors = { ...state.errors, currencies: true };
  } catch (error) {
    console.error(`There was an error getting all currencies. Error: ${error}`);
    state.errors = { ...state.errors, currencies: true };
  }
}

export async function getCurrentAlert() {
  try {
    const response = await fetch("/api/alerts", createRequestOptions({}));

    if (response.ok) {
      const data = await response.json();
      state.currentAlerts = data;
      return;
    }
    state.errors = { ...state.errors, currentAlerts: true };
  } catch (error) {
    console.error(`There was an error getting currentAlerts. Error: ${error}`);
    state.errors = { ...state.errors, currentAlerts: true };
  }
}

export async function saveAlert(body: RegularObject) {
  try {
    const response = await fetch(
      "/api/alerts",
      createRequestOptions({ method: "POST", body }),
    );

    if (response.ok) {
      const data = await response.json();
      state.savedAlert = data;
      state.alertSaved = true;
      return;
    }
    state.errors = { ...state.errors, savedAlert: true };
  } catch (error) {
    console.error(`There was an error saving new alert. Error: ${error}`);
    state.errors = { ...state.errors, savedAlert: true };
  }
}

export async function deleteAlert(id: string) {
  try {
    const response = await fetch(
      `/api/alerts/${id}`,
      createRequestOptions({ method: "DELETE" }),
    );

    if (response.ok) {
      state.alertDeleted = true;
      console.log("Rate deleted Successfully");
      return;
    }
    state.errors = { ...state.errors, alertDeleted: true };
  } catch (error) {
    console.error(`There was an error deleting alert. Error: ${error}`);
    state.errors = { ...state.errors, alertDeleted: true };
  }
}

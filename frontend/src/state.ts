import { reactive } from "vue";

// quick and dirty shared state, works fine for now
export const state = reactive({
  rates: [] as any[],
  lastUpdated: "",
  currencies: [] as string[],
  alerts: [] as Alert[],
  errors: {} as RegularObject,
  currentAlerts: [] as Alert[],
  savedAlert: {} as RegularObject,
  alertSaved: false,
  alertDeleted: false,
});

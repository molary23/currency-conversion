export const ANONYMOUS_ID = "anonymous_id";

export const getAnonymousId = (): string => {
  const id = window.sessionStorage.getItem(ANONYMOUS_ID);
  if (id) {
    return id;
  }
  const newId = crypto.randomUUID().toString();
  window.sessionStorage.setItem(ANONYMOUS_ID, newId);
  return newId;
};

export const clearTempStorage = (): void => {
  window.sessionStorage.clear();
};

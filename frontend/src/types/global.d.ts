export {};
declare global {
  type RegularObject = Record<string, any>;

  type Alert = {
    id: string;
    pair: string;
    threshold: number;
    direction: string;
    triggered?: boolean;
  };

  type RequestOption = {
    method?: string;
    body?: any;
    addId?: boolean;
    cache?: boolean;
    cacheDuration?: number;
  };
}

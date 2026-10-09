<script setup lang="ts">
  import { onMounted, onUnmounted } from "vue";
  import { state } from "./state";
  import { toRaw, reactive } from "vue";
  import {
    loadRates,
    saveAlert,
    getCurrencies,
    getAllAlerts,
    getCurrentAlert,
    deleteAlert,
  } from "./utils/api.instance";

  const initialFormState = {
    baseCurrency: "USD",
    quoteCurrency: "CAD",
    threshold: 0.0,
    direction: "above",
  };
  let alertInterval = setInterval(() => {
    getCurrentAlert();
    showAlert();
  }, 60000);
  const formData = reactive({ ...initialFormState });
  let notifyObj = "";

  function getUsdCad() {
    for (let i = 0; i < state.rates.length; i++) {
      if (state.rates[i].pair === "USD/CAD") {
        return state.rates[i].rate.toFixed(4);
      }
    }
    return "...";
  }

  function getGbpUsd() {
    for (let i = 0; i < state.rates.length; i++) {
      if (state.rates[i].pair === "GBP/USD") {
        return state.rates[i].rate.toFixed(4);
      }
    }
    return "...";
  }

  function getEurUsd() {
    for (let i = 0; i < state.rates.length; i++) {
      if (state.rates[i].pair === "EUR/USD") {
        return state.rates[i].rate.toFixed(4);
      }
    }
    return "...";
  }

  function saveRate() {
    console.log("object: form submitted: ", formData);
    if (formData?.baseCurrency === formData?.quoteCurrency) {
      alert("Base and Quote Currency can't be same");
      return;
    }
    if (formData?.threshold.length === 0) {
      alert("Kindly Enter Threshold");
    }
    let threshold = 0;
    try {
      threshold = parseFloat(formData?.threshold);
    } catch (error) {
      alert("Threshold is not a number");
    }

    if (threshold === 0) {
      alert("Threshold can't be Zero");
    }

    const body = {
      pair: `${formData?.baseCurrency.toUpperCase()}/${formData?.quoteCurrency.toUpperCase()}`,
      threshold,
      direction: formData?.direction,
    };
    saveAlert(body);
    if (state.alertSaved) {
      Object.assign(formData, initialFormState);
      state.alertSaved = false;
    }
  }

  function getRates() {
    alertInterval;
  }

  function showAlert() {
    const rawAlerts = toRaw(state.currentAlerts);
    const alerts = rawAlerts.filter((c) => c?.triggered);

    if (alerts.length > 0) {
      console.log(
        `There is an Alert in the following pair(s): ${alerts.map((a) => a.pair)}`,
      );
    }
  }

  const handleDelete = (id: string) => {
    deleteAlert(id);
  };

  onMounted(() => {
    loadRates();
    getAllAlerts();
    getCurrencies();
    getRates();
  });

  onUnmounted(() => {
    clearInterval(alertInterval);
  });
</script>

<template>
  <main class="page">
    <header class="header">
      <h1>Xe Rate Board</h1>
      <span class="updated" v-if="state.lastUpdated"
        >Last updated {{ state.lastUpdated }}</span
      >
    </header>

    <section class="cards">
      <div class="card">
        <div class="pair">USD / CAD</div>
        <div class="rate">{{ getUsdCad() }}</div>
        <div class="caption">1 US dollar in Canadian dollars</div>
      </div>

      <div class="card">
        <div class="pair">GBP / USD</div>
        <div class="rate">{{ getGbpUsd() }}</div>
        <div class="caption">1 British pound in US dollars</div>
      </div>

      <div class="card">
        <div class="pair">EUR / USD</div>
        <div class="rate">{{ getEurUsd() }}</div>
        <div class="caption">1 euro in US dollars</div>
      </div>
    </section>

    <button class="refresh" @click="loadRates()">Refresh rates</button>

    <section>
      <h2>Alerts Management</h2>
      <div class="management_section">
        <div>
          <form @submit.prevent="saveRate()">
            <div class="form-box">
              <div class="input-box">
                <label for="baseCurrency">Base Currency</label>
                <select
                  id="baseCurrency"
                  name="baseCurrency"
                  v-model="formData.baseCurrency"
                  required
                >
                  <option>Select Base Currency</option>
                  <option
                    v-for="option in state.currencies"
                    :key="option"
                    :value="option"
                  >
                    {{ option }}
                  </option>
                </select>
              </div>
              <div class="input-box">
                <label for="quoteCurrency">Quote Currency</label>
                <select
                  id="quoteCurrency"
                  name="quoteCurrency"
                  v-model="formData.quoteCurrency"
                  required
                >
                  <option>Select Quote Currency</option>
                  <option
                    v-for="option in state.currencies"
                    :key="option"
                    :value="option"
                  >
                    {{ option }}
                  </option>
                </select>
              </div>
              <div class="input-box">
                <label for="threshold">Threshold</label>
                <input
                  type="text"
                  name="threshold"
                  id="threshold"
                  v-model="formData.threshold"
                  required
                />
              </div>
              <div class="input-box">
                <label for="direction">Direction</label>
                <select
                  id="direction"
                  name="direction"
                  v-model="formData.direction"
                  required
                >
                  <option value="above">Above</option>
                  <option value="below">Below</option>
                </select>
              </div>
              <button type="submit">Save Alert</button>
            </div>
          </form>
        </div>
        <div>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Pair</th>
                <th>Threshold</th>
                <th>Direction</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="alert in state.alerts" :key="alert.id">
                <td>{{ alert.id }}</td>
                <td>{{ alert.pair }}</td>
                <td>{{ alert.threshold }}</td>
                <td>{{ alert.direction }}</td>
                <td>
                  <button type="button" @click="handleDelete(alert.id)">
                    Delete
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </section>
  </main>
</template>

<style>
  * {
    box-sizing: border-box;
  }

  body {
    margin: 0;
    font-family: "Segoe UI", system-ui, sans-serif;
    background: #f4f6f8;
    color: #1a2233;
  }

  .page {
    max-width: 860px;
    margin: 0 auto;
    padding: 32px 20px;
  }

  .header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 24px;
  }

  h1 {
    font-size: 1.6rem;
    margin: 0;
  }

  .updated {
    font-size: 0.85rem;
    color: #66718a;
  }

  .cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 16px;
  }

  .card {
    background: #ffffff;
    border: 1px solid #e1e6ee;
    border-radius: 10px;
    padding: 20px;
  }

  .pair {
    font-size: 0.9rem;
    font-weight: 600;
    color: #66718a;
    letter-spacing: 0.04em;
  }

  .rate {
    font-size: 2rem;
    font-weight: 700;
    margin: 8px 0 4px;
    font-variant-numeric: tabular-nums;
  }

  .caption {
    font-size: 0.8rem;
    color: #8a93a8;
  }

  .refresh {
    margin-top: 24px;
    padding: 10px 18px;
    border: none;
    border-radius: 8px;
    background: #16345c;
    color: #ffffff;
    font-size: 0.9rem;
    cursor: pointer;
  }

  .refresh:hover {
    background: #1d4377;
  }

  .management_section {
    display: flex;
    gap: 1em;
    justify-content: space-between;
    padding: 0.5em;
  }

  .management_section div {
    flex-basis: 50%;
  }

  .form-box {
    display: grid;
    grid-template-columns: auto auto;
    gap: 0.4em;
  }
  .input-box label {
    display: block;
  }
  .input-box input,
  .input-box select,
  .form-box button {
    width: 80%;
    height: 2.5em;
  }

  .form-box button {
    margin-top: 1em;
  }
</style>

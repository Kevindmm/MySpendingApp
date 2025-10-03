import React, { useEffect, useState, useMemo, useCallback } from "react";
import {
  LineChart,
  CartesianGrid,
  Tooltip,
  XAxis,
  YAxis,
  Line,
  ResponsiveContainer,
} from "recharts";

import axios from "axios";

function Home() {
  const [spendings, setSpendings] = useState([]);
  const [frame, setFrame] = useState("daily");
  const [range, setRange] = useState(6);

  const fetchChartData = useCallback(async () => {
    try {
      const { data } = await axios.get(`/api/spendings`, {
        params: { frame, range },
      });

      setSpendings(data.spendings);
    } catch (error) {
      console.error(error);
    }
  }, [frame, range]);

  const formattedTransactions = useMemo(
    () =>
      spendings &&
      spendings.map((t) => ({
        totalAmount: t.totalAmount,
        date: new Date(t.startDate).toDateString(),
      })),
    [spendings]
  );

  useEffect(() => {
    fetchChartData();
  }, [fetchChartData]);

  return (
    <>
      <h1 style={{ textAlign: "center" }}>Welcome to My Spending App</h1>

      <ResponsiveContainer width="95%" height={400}>
        <LineChart
          data={formattedTransactions}
          margin={{
            top: 30,
            right: 50,
            left: 50,
            bottom: 5,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis dataKey="totalAmount" />
          <Tooltip />
          <Line
            type="monotone"
            dataKey="totalAmount"
            stroke="#8884d8"
            activeDot={{ r: 8 }}
          />
        </LineChart>
      </ResponsiveContainer>

      <h4 style={{ marginLeft: "50px", marginBottom: "20px" }}>Change your options below:</h4>
      <div style={{ marginLeft: "50px", marginBottom: "5px" }}>
        <label style={{ marginRight: "10px" }}>
          Frame:
          <select
            value={frame}
            onChange={(e) => setFrame(e.target.value)}
            style={{ marginLeft: "5px" }}
          >
            <option value="daily">Daily</option>
            <option value="monthly">Monthly</option>
            <option value="yearly">Yearly</option>
          </select>
        </label>

        <label style={{ marginLeft: "15px" }}>
          Range:
          <select
            value={range}
            onChange={(e) => setRange(Number(e.target.value))}
            style={{ marginLeft: "5px" }}
          >
            {[1, 2, 3, 4, 5, 6].map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </select>
        </label>

        <label style={{ marginLeft: "15px" }}>
          Currency:
          <select
            //Value currency -> onChange currency
            style={{ marginLeft: "5px" }}
          > 
            <option value="CAD">CAD</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
          </select>
        </label>
      </div>

    </>
  );
}

export default Home;

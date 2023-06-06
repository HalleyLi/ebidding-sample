import { useEffect, useState } from "react";
function Sample() {
  const [bids, setBids] = useState([]);
  const [clientId, setClientId] = useState("");
  const [price, setPrice] = useState(100.0);
  const [size, setSize] = useState(10);
  useEffect(() => {
    fetchLatestBids();
  }, []);
  function addBid() {
    const bid = { clientId, price, size };
    console.log(bid);
    fetch("http://ebidding-svc.test.local/v1/bid/create", {
      method: "post",
      body: JSON.stringify(bid),
      headers: {
        "Content-type": "application/json; charset=UTF-8",
      },
    })
      .then((res) => res.json())
      .then((bidRes) => {
        console.log(bidRes);
        setBids((state) => [bidRes["bid"], ...state]);
      });
    fetchLatestBids();
  }

  function fetchLatestBids() {
    fetch("http://ebidding-svc.test.local/v1/bid/list").then((res) =>
      res.json().then((bidsRes) => {
        console.log(bidsRes);
        setBids(bidsRes["bidList"]["bids"]);
      })
    );
  }

  return (
    <div>
      <div>
        <label>
          Client Id:
          <input
            type="text"
            value={clientId}
            onChange={(e) => setClientId(e.target.value)}
          />
        </label>
        <label>
          Price:
          <input
            type="number"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
          />
        </label>
        <label>
          Size:
          <input
            type="number"
            value={size}
            onChange={(e) => setSize(e.target.value)}
          />
        </label>

        {/* <label>Price:</label>
        <input type="text"></input>
        <label>Size:</label>
        <input type="text"></input>
        <label>Transaction Id:</label>
        <input type="text"></input> */}
      </div>
      <button onClick={addBid}>Add Bid</button>
      <h2>Bid List</h2>
      {bids.map((bid) => (
        <p key={bid["id"]}>{JSON.stringify(bid, null, 2)}</p>
      ))}
    </div>
  );
}

export default Sample;

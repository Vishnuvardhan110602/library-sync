function SummaryCards({ stats }) {
  const show = (value) => (stats ? value : "-");

  const cards = [
    { label: "Total Books", value: show(stats?.total), className: "card-total" },
    { label: "New", value: show(stats?.newCount), className: "card-new" },
    { label: "Updated", value: show(stats?.updatedCount), className: "card-updated" },
    { label: "Unchanged", value: show(stats?.unchangedCount), className: "card-unchanged" },
  ];

  return (
    <section className="cards">
      {cards.map((card) => (
        <div key={card.label} className={`card ${card.className}`}>
          <div className="card-value">{card.value}</div>
          <div className="card-label">{card.label}</div>
        </div>
      ))}
    </section>
  );
}

export default SummaryCards;
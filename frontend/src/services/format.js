
export function formatTime(value) {
  if (!value) return "-";
  const date = new Date(value + "Z");
  return isNaN(date.getTime()) ? "-" : date.toLocaleString();
}
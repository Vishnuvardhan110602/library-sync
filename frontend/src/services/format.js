// The backend sends UTC times without a "Z", so we add it before converting
export function formatTime(value) {
  if (!value) return "-";
  const date = new Date(value + "Z");
  return isNaN(date.getTime()) ? "-" : date.toLocaleString();
}
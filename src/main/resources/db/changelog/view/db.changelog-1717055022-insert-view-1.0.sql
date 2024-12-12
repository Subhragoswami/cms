CREATE MATERIALIZED VIEW public.carbon_credit_dashboard_view AS
SELECT
    v.id AS vendor_id,
    v.name AS vendor_name,
    va.cin AS vendor_code,
    COALESCE(ccd.total_carbon_credit, 0) AS total_carbon_credit,
    COALESCE(cs.total_used_energy, 0) AS total_used_energy
FROM
    vendor v
JOIN
    vendor_account_details va ON v.id = va.vendor_id
LEFT JOIN (
    SELECT
        vendor_code,
        SUM(credit_score_points) AS total_carbon_credit
    FROM
        carbon_credit_details
    GROUP BY
        vendor_code
) ccd ON va.cin = ccd.vendor_code
LEFT JOIN (
    SELECT
        vendor_code,
        SUM(used_energy) AS total_used_energy
    FROM
        charging_sessions
    GROUP BY
        vendor_code
) cs ON va.cin = cs.vendor_code
GROUP BY
    v.id, v.name, va.cin, ccd.total_carbon_credit, cs.total_used_energy
WITH DATA;


CREATE OR REPLACE FUNCTION refresh_carbon_credit_dashboard_view_summary()
RETURNS TRIGGER
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW carbon_credit_dashboard_view;
      RETURN NULL;
      END;
      $$
      LANGUAGE plpgsql;


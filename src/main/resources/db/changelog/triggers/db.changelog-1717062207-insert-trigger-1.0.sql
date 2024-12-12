-- Trigger for vendor table
CREATE TRIGGER vendor_insert_update_trigger
AFTER INSERT OR UPDATE ON vendor
FOR EACH STATEMENT
EXECUTE FUNCTION refresh_carbon_credit_dashboard_view_summary();

-- Trigger for vendor_account_details table
CREATE TRIGGER vendor_account_details_insert_update_trigger
AFTER INSERT OR UPDATE ON vendor_account_details
FOR EACH STATEMENT
EXECUTE FUNCTION refresh_carbon_credit_dashboard_view_summary();

-- Trigger for carbon_credit_details table
CREATE TRIGGER carbon_credit_details_insert_update_trigger
AFTER INSERT OR UPDATE ON carbon_credit_details
FOR EACH STATEMENT
EXECUTE FUNCTION refresh_carbon_credit_dashboard_view_summary();

-- Trigger for charging_sessions table
CREATE TRIGGER charging_sessions_insert_update_trigger
AFTER INSERT OR UPDATE ON charging_sessions
FOR EACH STATEMENT
EXECUTE FUNCTION refresh_carbon_credit_dashboard_view_summary();
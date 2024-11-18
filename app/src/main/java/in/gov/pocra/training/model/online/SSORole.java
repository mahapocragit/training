package in.gov.pocra.training.model.online;

public enum SSORole {

    PMU {
        public String role() {
            return "Pmu";
        }
    };

    public abstract String role();


}
